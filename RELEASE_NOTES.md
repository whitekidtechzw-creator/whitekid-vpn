# 🧩 Whitekid VPN 2.0.2: "Servers on Connect + SlowDNS fixes"

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
- Version 2.0.2 (code 23)

⚠️ Not compiled here. If the build complains, send me the first red line of the log.

---

