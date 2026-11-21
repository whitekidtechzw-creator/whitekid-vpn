<div align="center">

<img src="assets/whitekid-vpn-logo.png" width="150" alt="Whitekid VPN logo">

# WHITEKID VPN
### Your connection. Your rules. Your tunnel.

**A powerful Android VPN client engineered by Whitekid Tech.**

<p>
  <img src="https://img.shields.io/badge/Version-2.0.8-ff6b35?style=for-the-badge" alt="Version 2.0.8">
  <img src="https://img.shields.io/badge/Platform-Android-3ddc84?style=for-the-badge&logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/Language-Kotlin-7f52ff?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin">
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285f4?style=for-the-badge" alt="Jetpack Compose">
</p>

**Multi-protocol connectivity • Smart reconnection • Privacy controls • Whitekid Labs**

[Telegram Community](https://t.me/whitekidtechzw) · [Report an issue](../../issues) · [Release notes](RELEASE_NOTES.md)

</div>

---

## 🌐 About Whitekid VPN

Whitekid VPN is an Android tunnelling application developed under the **Whitekid Tech** brand. It brings multiple VPN and tunnelling technologies into one interface, with configurable server profiles, connection monitoring, DNS tools and protection features.

Import a supported configuration, add a server manually, or create and share a `.wkp` profile. The connection supervisor monitors tunnel health and can retry after interruptions.

> **Important:** Whitekid VPN is a client application, not a provider of free internet or guaranteed working servers. You need a valid server/configuration and permission to use it. Connection speed, availability and access depend on your network, server and provider.

## 🖼️ Project visuals

<div align="center">
  <img src="assets/whitekid-vpn-logo.png" width="220" alt="Whitekid VPN project logo">
  <br><sub>Official project logo included in this repository.</sub>
  <br><br>
  <img src="assets/whitekid-vpn-app-icon.png" width="120" alt="Whitekid VPN Android app icon">
  <br><sub>Android launcher foreground artwork.</sub>
</div>

<!-- Add genuine in-app screenshots to assets/screenshots/ and uncomment the example below.
<div align="center">
  <img src="assets/screenshots/home.png" width="30%" alt="Home screen">
  <img src="assets/screenshots/servers.png" width="30%" alt="Server list">
  <img src="assets/screenshots/labs.png" width="30%" alt="Whitekid Labs">
</div>
-->

## ⚡ Key features

### 🔌 Multi-protocol engines

| Protocol / technology | Implementation in this project |
|---|---|
| VLESS | Xray / libv2ray |
| VMess | Xray / libv2ray |
| Trojan | Xray / libv2ray |
| Shadowsocks | Xray / libv2ray |
| WireGuard | WireGuard userspace Go backend |
| SSH | JSch |
| SlowDNS | dnstt client with SSH tunnel |

Xray-based profiles support relevant transports and security options such as TCP, WebSocket, gRPC, TLS, SNI and Reality where supported by the selected protocol/core configuration.

### 🛡️ Connection and privacy controls

- Connection state is verified against real tunnel traffic before the app reports a successful connection.
- Automatic reconnect and retry supervision.
- Auto-heal attempts that can adjust tunnel parameters between retries.
- Kill-switch mode to block traffic while a tunnel is being rebuilt.
- IPv6 leak protection option.
- Secure DNS / DNS configuration options and DNS-based blocking.
- Split tunnelling and per-app routing controls.
- App PIN and screenshot protection options.
- Connection health checks and diagnostic tools.

### 🧩 Configuration management

- Add supported server types using dedicated configuration fields.
- Import supported links and `.wkp` configuration files.
- Share profiles using QR codes and shareable codes.
- Profile expiry, data limits, password and device-lock options are supported by the profile format.
- Backup and restore tools.
- Configuration links can be refreshed where configured.

### 📡 Network monitoring

- Live connection status and traffic information.
- Ping and link-quality checks.
- Network watcher and reconnect handling.
- Connection doctor and support-report tools.
- Usage history and data-budget utilities.

### 🧪 Whitekid Labs

The Labs area contains network and utility tools, including IP/ISP information, DNS and IP lookups, website and port checks, latency and speed tests, ping monitoring, configuration inspection, generators, usage reports, server inventory and health checks.

The exact tools available can vary by build and device.

### 🎨 Interface

- Jetpack Compose and Material 3 interface.
- Light, dark and AMOLED-style themes.
- Dashboard-style connection controls and server management.
- Designed for practical use on Android phones.

## 🧰 Technology stack

- **Kotlin** — primary application language
- **Jetpack Compose / Material 3** — user interface
- **Kotlin Coroutines** — asynchronous work
- **Xray / libv2ray** — supported proxy protocol engine
- **WireGuard userspace backend** — WireGuard tunnelling
- **JSch** — SSH connectivity
- **dnstt** — DNS tunnelling component
- **ZXing** — QR-code utilities
- **Gradle + GitHub Actions** — build automation

## 📱 Compatibility

- Android application.
- Project build configuration targets Android 8.0+ (API 26+).
- Native ABI filters in the supplied build configuration: `arm64-v8a` and `armeabi-v7a`.

Compatibility can also depend on Android version, device firmware, CPU architecture and the availability of required native libraries.

## 🚀 Build the project

This source package is supplied in a **flat-upload format**. Files containing double underscores (`__`) represent directory separators and are arranged into their normal project paths by the included GitHub Actions workflow.

### GitHub Actions build

1. Create or open the GitHub repository for this project.
2. Upload the project files from the supplied package, preserving their filenames.
3. Place the workflow at `.github/workflows/build.yml` as required by your repository setup.
4. Open **Actions** and run the **Build APK** workflow.
5. When the workflow succeeds, download the generated APK artifact.

### Optional GitHub Actions secrets

| Secret | Purpose |
|---|---|
| `WK_KEYSTORE_B64` | Base64-encoded Android release keystore |
| `WK_STORE_PASS` | Keystore password |
| `WK_KEY_ALIAS` | Signing key alias |
| `WK_KEY_PASS` | Signing key password |
| `WK_SLOWDNS_NS` | Built-in SlowDNS nameserver |
| `WK_SLOWDNS_KEY` | Built-in SlowDNS public key |
| `WK_SLOWDNS_USER` | Built-in SlowDNS SSH username |
| `WK_SLOWDNS_PASS` | Built-in SlowDNS SSH password |
| `WK_SSL_SERVERS_B64` | Optional Base64-encoded SSL server JSON data |

**Security:** Never commit keystores, passwords, private keys, server credentials or other secrets to a public repository. Configure them as repository Actions secrets. Use only servers and credentials you own or are authorised to access.

## 👥 Team & collaborators

### Project owner / lead developer

**Whitekid Tech**  
Project: **Whitekid VPN**  
Role: Project owner, product direction and lead maintainer.

### Contributors

No additional individual collaborator names were identified in the supplied source package. Rather than inventing credits, this README recognises contributors through the repository's commit history.

Want to contribute? Pull requests, bug reports, testing feedback and documentation improvements are welcome. Once a contribution is merged, add the contributor's name or GitHub handle here.

| Contributor | Contribution |
|---|---|
| Whitekid Tech | Project ownership and maintenance |
| Community contributors | Add names and contribution details after verified contributions |

### Thanks & acknowledgements

Whitekid VPN builds on open-source technologies including the Kotlin, AndroidX / Jetpack Compose, Xray, WireGuard, JSch, dnstt and ZXing ecosystems. Their respective maintainers and contributors make this project possible. Please consult the relevant upstream project licences and notices when redistributing builds.

## 🗺️ Roadmap

Potential areas for continued development:

- More connection diagnostics and clearer failure explanations.
- More profile import/export compatibility.
- Expanded server health and latency reporting.
- Improved accessibility and localisation.
- More device and Android-version testing.
- Clearer documentation for building and configuring server profiles.

## 🐛 Issues, support & community

Found a bug or have an improvement idea?

1. Check existing repository issues before opening a new one.
2. Include your Android version, device model, app version and a description of the issue.
3. Remove passwords, private keys, UUIDs and other sensitive configuration data from logs or screenshots before sharing.
4. For community contact, visit the Whitekid Tech Telegram channel:

**[Join Whitekid Tech on Telegram](https://t.me/whitekidtechzw)**

## 📜 Version

**Current source version: 2.0.8**

See [`RELEASE_NOTES.md`](RELEASE_NOTES.md) for the supplied release notes.

## 📄 Licence

A licence file was not present in the supplied source archive. Until the project owner adds a `LICENSE` file, do not assume that the code is open for unrestricted reuse, modification or redistribution. Add the intended licence to the repository before publishing a formal open-source release.

---

<div align="center">

### WHITEKID TECH
**Building tools. Connecting possibilities.**

*The future had already come.*

</div>
