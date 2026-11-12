# 🔌 Whitekid VPN 1.13.0: "SSL / SNI mode"

**Pick a country, pick a port, type only an SNI, tap CONNECT.** (Connect screen, new SSL / SNI MODE card.)
- Runs SSH inside TLS with your SNI (engine already in the app; nothing else to fill in)
- **TEST** proves the whole chain: TCP, TLS with your SNI, and the SSH banner
- **AUTO PORT** tries every port of the country and picks the first that works
- Recent SNIs are remembered as chips
- Countries come from: GitHub secret `WK_SSL_SERVERS_B64`, the file `app__src__main__assets__ssl_servers.json`, or **+ COUNTRY** inside the app
- JSON format: `[{"country":"South Africa","flag":"🇿🇦","host":"za1.example.com","ports":[443,8443,80],"user":"name","pass":"secret","sni":"optional default"}]`
- Version 1.13.0 (code 14). New file `Labs5.kt`; small edits in `MainActivity.kt`, `app__build.gradle.kts`, `build.yml`

⚠️ Not compiled here. The SSL server itself (stunnel, nginx stream or similar in front of SSH) must exist: the app cannot create servers.

---

# 🔌 Whitekid VPN 1.12.0: "Cockpit II + DNS fix"

## 🛠 DNS error fixed ("DNS 8.8.8.8 isn't answering")
**Cause:** after connecting, the app asked ONE resolver ONE time with a 5 s limit. Over SSH and SlowDNS the first answer takes longer, and some SSH servers refuse raw port 53 even though names resolve fine. So a working VPN showed a red error.
**Fix (`DnsCheck.kt`, `WhitekidVpnService.kt`):**
- Waits for the tunnel to settle, asks your DNS twice with patient limits (15 s on SlowDNS)
- Then tries backups: 8.8.8.8, 8.8.4.4, 1.1.1.1, 9.9.9.9
- Last proof: a real request by name through the tunnel. If names resolve, it says "DNS working"
- Repeats up to 3 times; the red warning appears only if ALL of that fails
- New **RETEST DNS** button and one-tap **Google / Cloudflare / Quad9** switch (rebuilds the tunnel to apply it)
- The tunnel itself still uses Google DNS (8.8.8.8, 8.8.4.4) with backup resolvers, as before

## 🏠 Connect screen (new)
1. DNS panel: RETEST DNS + Google / Cloudflare / Quad9 chips
2. Quality SCORE 0-100 with a bar
3. COPY IP and COPY REPORT (status, ping, DNS, last log lines)
4. Keep screen on (screen only)
5. Quick switches: Kill switch, Auto-reconnect, Block IPv6, Secure DNS
6. Daily data alert chips (100 MB to 2 GB)
7. Last 7 days bar chart with total, average, best day

## 🖧 Servers screen (new)
8. **Remove unreachable (N)** with undo
9. **Random** reachable server
10. **Paste link** straight from the clipboard
11. **Copy backup** of all servers as one code
12. Sort by **Used** data, and "Used so far" on each server
13. List facts line: average ping, expiring soon, total used

New file `app__src__main__java__zw__whitekid__vpn__ui__Labs4.kt`. Version 1.12.0 (code 13).

## ⚠️ Heads-up
Not compiled here (no Android toolchain). If GitHub Actions fails, send the build log and I will fix it.

---

# 🔌 Whitekid VPN 1.11.0: "Cockpit Upgrade"

**20 new features on the Connect and Servers screens. UI only: the tunnel and service are untouched.**

## 🏠 Connect screen
1. **Link quality verdict**: Excellent, Good, Fair or Poor, with a plain tip (SlowDNS gets its own, kinder thresholds)
2. **AVG SPEED and PEAK** readouts for the session
3. **CONNECTED IN**: how many seconds the connect took
4. **Ping trend** sparkline with best and worst
5. **Expiry and data warnings** for the selected server (3 days left, 80% used, expired, limit reached)
6. **Monthly budget bar** (uses the budget set in Labs II, item 30)
7. **Yesterday vs today** usage with a ▲ or ▼ percentage
8. **FIND FASTEST**: pings your network's servers and selects the quickest
9. **Sleep timer chips** right on Connect (same setting as Tools; counts from the next connect)
10. **SHARE STATS** (duration, data, speed, ping; no server name or IP) and a **Last session** card with lifetime totals when idle

## 🖧 Servers screen
11. **Favourites filter** and 12. **protocol filter** chips
13. Three new sort modes: **Expiry**, **Reliable**, **Group**
14. **Undo** for deleted servers (7 seconds)
15. **Remove expired (N)** in one tap, with undo
16. **Remove duplicates (N)** in one tap, with undo
17. **Data limit progress bar** on servers that have a limit
18. **Reliability %** shown on each server
19. **Expiry colours**: amber when under 3 days, red when expired
20. **Tap a ping to re-test** that one server, plus a **summary line** (servers, reachable, fastest)

## 🔧 Under the hood
- New file `app__src__main__java__zw__whitekid__vpn__ui__Labs3.kt`
- `MainActivity.kt`: small insertions in `HomeScreen` and `ServersScreen`
- Version bumped to **1.11.0** (code 12)

## ⚠️ Heads-up
Not compiled or tested yet (no Android toolchain available here). If the GitHub Actions run fails, send the build log.

---

## 1.10.0: "Labs II"

**30 new tools. One new look option. Zero changes to how your tunnel connects.**

Everything from 1.9 is untouched. The new tools live in **Tools → Labs II**, numbered 11 to 40, in four groups.

## 🌐 Network (11–19)
- **11 Network details**: connection type, addresses, DNS, MTU, Private DNS, metered or not
- **12 DNS lookup**: resolve any domain, with timing
- **13 IP lookup**: city, country and network of any IP
- **14 Latency map**: ping Cloudflare, Google, WhatsApp, TikTok and more in one go
- **15 Speed test**: download and upload (uses about 7 MB, with a warning first)
- **16 Speed history**: your last 20 tests, best and average
- **17 Clock check**: phone time vs real time, because wrong clocks break expiring files and TLS
- **18 Ping watchlist**: save your own hosts and test them all
- **19 Private DNS check**: spots a setting that can break lookups

## 🖧 Servers (20–28)
- **20 Rank by ping**, **21 Reliability scoreboard**
- **22 Remove duplicates**, **23 Delete a whole group**
- **24 Inventory**: counts by protocol, expiry and group
- **25 Share list as CSV** (locked hosts hidden, no secrets)
- **26 Copy backup code**
- **27 Reset data counters** (servers with a creator-set data limit are never reset)
- **28 Reorder the list**: A–Z, soonest expiry, by protocol

## 📊 Data (29–32)
- **29 30-day report**: total, busiest day, average, streak and a sparkline
- **30 Monthly budget**: progress bar, month-end forecast, safe daily allowance
- **31 Cost calculator**: enter bundle price and GB to see what your usage costs
- **32 Usage CSV**

## 🧰 Setup and utilities (33–40)
- **33 Health check** with one-tap Fix buttons (battery, notifications, Data Saver, Private DNS)
- **34 Settings shortcuts**
- **35 Base64** encode and decode
- **36 Hash generator**: SHA-256, SHA-1, MD5
- **37 Password generator Pro**: length, character sets, strength estimate
- **38 Link inspector**: parses a pasted server link and warns about missing TLS, self-signed certificates, missing Reality key or SNI
- **39 Share the app** QR
- **40 AMOLED pure black** mode

## 🔧 Under the hood
- `Store.kt`: helper storage for the new tools, AMOLED flag, dated usage list (wiped by "Reset the app" like everything else)
- `Theme.kt`: AMOLED colours, status and navigation bars follow the toggle
- `MainActivity.kt`: one line to show the new section
- Version bumped to **1.10.0** (code 11)

## ⚠️ Heads-up
- This build had **not been compiled or tested** when these notes were written (no Android toolchain was available). If the GitHub Actions run fails, send the build log.
- Network tests show results for the network you are on, so run them while connected to test your VPN.

## 📥 Upgrade
Upload every file from the zip (new file: `app__src__main__java__zw__whitekid__vpn__ui__Labs2.kt`), replace `build.yml`, then run **Actions → Build APK**.

---

## Earlier versions

**1.9.0**: Labs I: IP and ISP info, Cloudflare trace, website checker, port checker, jitter and packet loss, expiry tracker, remove expired servers, UUID and password generator, usage report, clear cache.

**1.8.0**: Full redesign (carbon and bone, signal-orange accent, gauge dial, monospace readouts). Shield levels, a real kill switch, protection check, live log on Home, signal bars on Servers.

**1.7.0**: Connect fix for SSH and SlowDNS with auto-heal retries. Network watcher, live speed graph, link health, ad-block DNS, auto-connect network rule, daily data alert, session report, 7-day chart, ping tool, support report.

**1.6.0**: CONNECTING → CONNECTED flow with proven traffic. Tunnel never gives up and resumes when the network returns. Stronger SlowDNS with resolver failover. 30 features.
