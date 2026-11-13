package zw.whitekid.vpn.vpn

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Base64
import zw.whitekid.vpn.data.AppLog
import zw.whitekid.vpn.data.Profile
import java.io.File
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/**
 * SlowDNS (dnstt): runs the bundled dnstt-client binary, which tunnels TCP through DNS queries and
 * listens on 127.0.0.1:[port]. SshEngine then logs in to the SSH server behind it.
 *
 * Resolver failover: Google DNS first (8.8.8.8, then 8.8.4.4), then Cloudflare and Quad9, then the DNS servers of the real
 * network (the ISP's own). Every resolver is probed at the same time; each new attempt moves on to the next one that answers,
 * so one blocked resolver never stops SlowDNS.
 *
 * v2.0.2 hardening: a free local port is picked on every start (a leftover process can no longer block a restart), the server
 * key is cleaned up (spaces, 0x, base64) before it reaches dnstt, and the last line dnstt printed is shown when it stops.
 */
class DnsttEngine(private val ctx: Context, private val p: Profile) {
    companion object {
        const val PORT = 7000
        val RESOLVERS = listOf("8.8.8.8", "8.8.4.4", "1.1.1.1", "9.9.9.9")
        private val HEX64 = Regex("^[0-9a-fA-F]{64}$")

        /** dnstt wants the server key as 64 hex characters. Accepts stray spaces / new lines, a 0x prefix, or the base64 form. */
        fun normalizeKey(raw: String): String {
            val s = raw.trim().removePrefix("0x").filter { !it.isWhitespace() }
            if (HEX64.matches(s)) return s.lowercase()
            val bytes = runCatching { Base64.decode(s.replace('-', '+').replace('_', '/'), Base64.DEFAULT) }.getOrNull()
            if (bytes != null && bytes.size == 32) return bytes.joinToString("") { "%02x".format(it) }
            error("The SlowDNS public key must be 64 hex characters (this one has ${s.length}). Copy the whole key from your server.")
        }
    }

    /** the local port the tunnel listens on (picked free at start) */
    @Volatile var port: Int = PORT
        private set
    @Volatile private var proc: Process? = null
    @Volatile private var lastLine = ""

    /** false once the dnstt process has exited (the watchdog in VpnController then restarts everything) */
    fun isAlive(): Boolean {
        val pr = proc ?: return false
        return try { pr.exitValue(); false } catch (_: IllegalThreadStateException) { true }
    }

    private fun freePort(): Int =
        runCatching { ServerSocket(0, 1, InetAddress.getByName("127.0.0.1")).use { it.localPort } }.getOrDefault(PORT)

    /** DNS servers of the real network(s), not the VPN: the ISP's own resolvers are the last fallback. */
    private fun ispDns(): List<String> = runCatching {
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.allNetworks.flatMap { n ->
            val caps = cm.getNetworkCapabilities(n)
            if (caps == null || caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) emptyList<String>()
            else cm.getLinkProperties(n)?.dnsServers?.mapNotNull { it.hostAddress }.orEmpty()
        }.filter { !it.contains(':') && !it.startsWith("10.77.") }.distinct()
    }.getOrDefault(emptyList())

    fun start(attempt: Int = 0) {
        val bin = File(ctx.applicationInfo.nativeLibraryDir, "libdnstt.so")
        if (!bin.exists()) error("SlowDNS engine is missing from this build")
        val key = normalizeKey(p.publicKey)
        val domain = p.host.trim().trim('.')
        if (domain.isEmpty() || DnsResolve.isIp(domain)) error("SlowDNS needs the nameserver domain (like ns.example.com), not an IP address")
        port = freePort()
        lastLine = ""
        val own = p.resolver.trim().takeIf { DnsResolve.isIp(it) && !it.contains(':') }
        val all = (listOfNotNull(own) + RESOLVERS + ispDns()).distinct()
        val up = DnsResolve.aliveList(all).ifEmpty { all }
        val pick = up[attempt % up.size]
        AppLog.log("SlowDNS: resolver $pick (${up.size} of ${all.size} answering, try ${attempt + 1}), local port $port")
        val pb = ProcessBuilder(bin.absolutePath, "-udp", "$pick:53", "-pubkey", key, domain, "127.0.0.1:$port")
            .redirectErrorStream(true)
        if (p.locked) pb.redirectOutput(ProcessBuilder.Redirect.appendTo(File("/dev/null")))
        val pr = pb.start()
        proc = pr
        if (!p.locked) thread(name = "dnstt-log", isDaemon = true) {
            runCatching { pr.inputStream.bufferedReader().forEachLine { if (it.isNotBlank()) lastLine = it; AppLog.log("dnstt: $it") } }
        }
        // DNS tunnels are slow to wake up, so be patient
        val end = System.currentTimeMillis() + 25_000
        while (System.currentTimeMillis() < end) {
            if (!isAlive()) {
                val why = lastLine.takeIf { it.isNotBlank() && !p.locked }
                error("SlowDNS engine stopped" + (why?.let { ": $it" } ?: ". Check the nameserver and public key"))
            }
            try {
                Socket().use { it.connect(InetSocketAddress("127.0.0.1", port), 300) }
                return
            } catch (_: Exception) { Thread.sleep(250) }
        }
        error("SlowDNS didn't start in time")
    }

    fun stop() {
        val pr = proc; proc = null
        if (pr != null) runCatching {
            pr.destroy()
            if (!pr.waitFor(1500, TimeUnit.MILLISECONDS)) pr.destroyForcibly()
        }
    }
}
