# 🧩 Whitekid VPN 2.0.7: "SlowDNS for WhatsApp"

- SlowDNS now carries WhatsApp only (unless Split tunnel is on): Huawei/background apps were opening dozens of connections and starving the tiny DNS tunnel
- SlowDNS is no longer torn down by slow link tests: tests run every 45 s, 12 misses are needed, and connecting no longer waits 20 s

---

# 🧩 Whitekid VPN 2.0.6: "Build fix + SSH channel log"

- Build: Kotlin compiler now runs inside Gradle (fixes "The daemon has terminated unexpectedly"), setup-java v5
- SlowDNS/SSH: the live log shows each connection the server opens or refuses, with the reason and time, so a blocked destination is visible

---

# 🧩 Whitekid VPN 2.0.5: "SlowDNS names resolve"

- SlowDNS: the phone's names (WhatsApp, browsers) are now resolved by the phone's own resolver first, outside the slow tunnel, instead of through a server that may refuse port 53

---

# 🧩 Whitekid VPN 2.0.4: "SlowDNS stays up"

- SlowDNS: a Disconnect during start-up no longer leaves a hidden dnstt process running (it fought the new one for the resolver and broke the tunnel); leftovers are killed on every start
- SlowDNS: no DNS-over-HTTPS and fewer backup resolvers through the tunnel, DNS checks stop after the first instant refusal and wait for the first test, so the slow link is not flooded

---

# 🧩 Whitekid VPN 2.0.3: "Share code keeps the note"

- Copy share code / QR now keeps the server's own note, and importing a code no longer wipes a server's note, expiry, data limit or lock when the file itself has none

---

# Whitekid VPN 2.0.2: "Servers on Connect + SlowDNS fixes"

**Servers**
- The full Servers screen is gone. Only the Add server dialog is kept (paste, import file, create tweak), opened with + ADD on the Connect screen, the + on the NETWORK card, or the side menu
- Server cards now do everything: tap to select, ★ to favourite, ⋮ or long-press for rename / duplicate / copy / QR / reset usage / delete (with Undo)
- Sort (Added, Name, Ping, Expiry, Reliable, Group, Used), ★ Favourites filter, protocol filter, search, ping, fastest badge
- Tools row: select fastest, free WARP, random, remove expired / duplicates / unreachable, paste, backup, create file, update links
- Data limit bar and usage line on each card

**SlowDNS**
- A free local port is picked on every start, so a leftover dnstt process can no longer block a reconnect (old fixed port 7000)
- The server key is cleaned before use (spaces, 0x prefix, base64 form turned into the 64 hex characters dnstt needs) and a clear message appears if it is wrong
- The ISP's own DNS servers are now the last fallback after Google 8.8.8.8, 8.8.4.4, 1.1.1.1 and 9.9.9.9
- When dnstt stops, its last message is shown in the error
- SSH login also accepts older servers (older key exchange, host key, cipher and MAC) after the modern ones, which fixes "Algorithm negotiation fail" on old Dropbear / OpenSSH servers (also helps plain SSH and SSL profiles)
- Version 2.0.3 (code 24)

⚠️ Not compiled here. If the build complains, send me the first red line of the log.

---

