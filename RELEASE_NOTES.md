# 🔧 Whitekid VPN 2.0.8: "Real configs, real APIs"

**Add server, like other VPN apps**
- VLESS / VMess / Trojan: proper fields now (address, port, UUID/password, network tcp/ws/grpc, path, TLS, SNI, VLESS flow, VMess alter ID) instead of a raw link box
- Shadowsocks: address, port, a cipher picker (aes-256-gcm, aes-128-gcm, chacha20-ietf-poly1305, 2022-blake3-aes-256-gcm), password
- WireGuard: private key, address, DNS, peer public key, preshared key, endpoint, allowed IPs — or paste a full config instead
- Pasting a link or a shared code still works exactly as before, these are just a second, friendlier way in

**APIs refreshed**
- Free WARP registration now sends the registration version and client header current WireGuard-based WARP tools use, and connects to the IP Cloudflare actually assigns the peer instead of a fixed hostname

**Fixes**
- Server list no longer says "0 reachable" for SlowDNS or WireGuard servers (they can't be pinged before connecting, so it now says that plainly instead of looking broken)

⚠️ Not compiled here. If the build complains, send me the first red line of the log.

---

