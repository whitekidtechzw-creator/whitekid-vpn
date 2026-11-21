package zw.whitekid.vpn.vpn

import com.wireguard.crypto.KeyPair
import org.json.JSONObject
import zw.whitekid.vpn.data.Profile
import zw.whitekid.vpn.data.Proto
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Free Cloudflare WARP over WireGuard: registers a fresh account and builds a WireGuard profile. Call off the main thread. */
object Warp {
    fun create(): Profile {
        val kp = KeyPair()
        val priv = kp.privateKey.toBase64(); val pub = kp.publicKey.toBase64()
        val iso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date())
        val body = JSONObject().put("key", pub).put("install_id", "").put("fcm_token", "").put("tos", iso)
            .put("type", "Android").put("model", "PC").put("locale", "en_US").toString()
        // API path / CF-Client-Version drift over time; v0a2483 is what current WARP clients send as of late 2026
        val c = URL("https://api.cloudflareclient.com/v0a2483/reg").openConnection() as HttpURLConnection
        try {
            c.requestMethod = "POST"; c.doOutput = true; c.connectTimeout = 15000; c.readTimeout = 15000
            c.setRequestProperty("Content-Type", "application/json")
            c.setRequestProperty("CF-Client-Version", "a-7.21-0721")
            c.setRequestProperty("User-Agent", "okhttp/3.12.1")
            c.outputStream.use { it.write(body.toByteArray()) }
            if (c.responseCode !in 200..299) error("Cloudflare replied ${c.responseCode}")
            val j = JSONObject(c.inputStream.bufferedReader().use { it.readText() })
            val cfg = j.getJSONObject("config")
            val v4 = cfg.getJSONObject("interface").getJSONObject("addresses").getString("v4")
            val peer = cfg.getJSONArray("peers").getJSONObject(0)
            val peerKey = peer.getString("public_key")
            // use the IP Cloudflare actually assigned this peer, not the hostname (it can resolve to the wrong anycast IP)
            val ep = peer.optJSONObject("endpoint")
            val epIp = ep?.optString("v4")?.substringBefore(":")?.takeIf { it.isNotBlank() } ?: "162.159.192.1"
            val epPort = ep?.optString("host")?.substringAfterLast(":")?.toIntOrNull() ?: 2408
            val conf = "[Interface]\nPrivateKey = $priv\nAddress = $v4/32\nMTU = 1280\n\n" +
                "[Peer]\nPublicKey = $peerKey\nAllowedIPs = 0.0.0.0/0\nEndpoint = $epIp:$epPort\n"
            return Profile(name = "Cloudflare WARP (free)", proto = Proto.WIREGUARD, host = epIp, port = epPort, raw = conf)
        } finally { c.disconnect() }
    }
}
