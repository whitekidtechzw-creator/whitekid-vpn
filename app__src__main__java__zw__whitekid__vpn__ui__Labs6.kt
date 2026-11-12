package zw.whitekid.vpn.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import zw.whitekid.vpn.R
import zw.whitekid.vpn.data.Profile
import zw.whitekid.vpn.data.Store
import zw.whitekid.vpn.vpn.Pinger
import zw.whitekid.vpn.vpn.Status

/**
 * v1.14.0: servers live on the Connect screen. Cards for every server of the chosen network: tap one to select it,
 * ping chips refresh by themselves, "MANAGE" opens the full server manager (import, sort, backup, ...).
 */
@Composable
fun ServerDeck(
    all: List<Profile>, group: String, selectedId: String?, autoPick: Boolean, status: Status,
    onPick: (String) -> Unit, onManage: () -> Unit
) {
    val inGroup = all.filter { group == "All" || it.groupName() == group }
    val pings = remember { mutableStateMapOf<String, Int>() }
    var tick by remember { mutableIntStateOf(0) }
    var busy by remember { mutableStateOf(false) }
    var showAll by remember { mutableStateOf(false) }
    val key = inGroup.joinToString(",") { it.id }
    LaunchedEffect(key, tick) {
        if (inGroup.isEmpty() || status == Status.CONNECTED || status == Status.CONNECTING) return@LaunchedEffect
        busy = true
        val res = inGroup.map { p -> async(Dispatchers.IO) { p.id to runCatching { Pinger.tcp(p) }.getOrDefault(-1) } }.awaitAll()
        res.forEach { (id, ms) -> pings[id] = ms }
        busy = false
    }
    val limit = 5
    val shown = if (showAll) inGroup else inGroup.take(limit)

    Spacer(Modifier.height(20.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("SERVERS", color = WkMint, fontWeight = FontWeight.Black, fontSize = 12.sp, letterSpacing = 3.sp)
        Spacer(Modifier.width(8.dp))
        Box(Modifier.clip(RoundedCornerShape(10.dp)).background(WkMint.copy(alpha = 0.16f)).padding(horizontal = 8.dp, vertical = 2.dp)) {
            Text("${inGroup.size}", color = WkMint, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
        Spacer(Modifier.weight(1f))
        if (inGroup.isNotEmpty()) TextButton(onClick = { if (!busy) tick++ }) {
            Text(if (busy) "TESTING…" else "↻ PING", fontSize = 11.sp, letterSpacing = 1.sp, color = WkMuted)
        }
        TextButton(onClick = onManage) { Text("MANAGE ›", fontSize = 11.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold) }
    }
    Box(Modifier.fillMaxWidth().height(1.dp).background(Brush.horizontalGradient(listOf(WkMint.copy(alpha = 0.7f), Color.Transparent))))
    Spacer(Modifier.height(10.dp))

    if (inGroup.isEmpty()) {
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).border(1.dp, WkMint.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .background(WkMint.copy(alpha = 0.07f)).clickable(onClick = onManage).padding(18.dp)) {
            Column {
                Text("+  Add your first server", fontWeight = FontWeight.Bold, color = WkMint)
                Text("Paste a link, import a .wkp file or add free WARP", color = WkMuted, fontSize = 12.sp)
            }
        }
        return
    }

    // AUTO card
    DeckCard(
        sel = autoPick, badge = "⚡", title = "Auto select", sub = "Always the fastest server, picked at connect",
        ping = null, dim = false, tag = if (autoPick) "ON" else "", onClick = { onPick("auto") }
    )
    shown.forEach { p ->
        Spacer(Modifier.height(8.dp))
        val ms = pings[p.id]
        DeckCard(
            sel = !autoPick && p.id == selectedId,
            badge = p.proto.label.take(2).uppercase(), title = p.name,
            sub = "${p.proto.label} • ${p.detail()}" + (p.expiryText()?.let { " • $it" } ?: ""),
            ping = ms, dim = p.expired(), tag = if (p.expired()) "EXPIRED" else if (p.id in Store.favorites) "★" else "",
            onClick = { onPick(p.id) }
        )
    }
    if (inGroup.size > limit) {
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            TextButton(onClick = { showAll = !showAll }) {
                Text(if (showAll) "SHOW LESS ▴" else "SHOW ALL ${inGroup.size} ▾", fontSize = 11.sp, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DeckCard(sel: Boolean, badge: String, title: String, sub: String, ping: Int?, dim: Boolean, tag: String, onClick: () -> Unit) {
    val a by animateFloatAsState(if (sel) 1f else 0f, tween(280), label = "deckSel")
    val shape = RoundedCornerShape(16.dp)
    Row(
        Modifier.fillMaxWidth().alpha(if (dim) 0.5f else 1f).clip(shape)
            .background(Brush.horizontalGradient(listOf(lerp(WkCard, WkMint, 0.20f * a), lerp(WkCard, WkMint, 0.04f * a))))
            .border(if (sel) 1.5.dp else 1.dp, lerp(WkLine, WkMint, a), shape)
            .clickable(onClick = onClick).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(WkMint.copy(alpha = 0.12f + 0.2f * a)), contentAlignment = Alignment.Center) {
            Text(badge, color = WkMint, fontWeight = FontWeight.Black, fontSize = 13.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, maxLines = 1, color = WkText)
            Text(sub, color = WkMuted, fontSize = 11.sp, maxLines = 1, fontFamily = FontFamily.Monospace)
        }
        if (tag.isNotEmpty()) {
            Text(tag, color = if (tag == "EXPIRED") WkRed else WkMint, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp,
                modifier = Modifier.padding(end = 8.dp))
        }
        if (ping != null) {
            Column(horizontalAlignment = Alignment.End) {
                SignalBars(ping)
                Text(if (ping >= 0) "$ping ms" else "—", color = if (ping < 0) WkMuted else quality(ping, false).second,
                    fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

/** Screen opened from the side menu (servers manager, tools, about) with a way back to Connect. */
@Composable
fun SubScreen(onBack: () -> Unit, content: @Composable () -> Unit) {
    BackHandler(onBack = onBack)
    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth().padding(start = 6.dp, end = 16.dp, top = 2.dp, bottom = 2.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = WkMint) }
            Text("CONNECT", color = WkMuted, fontSize = 11.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(Brush.horizontalGradient(listOf(Color.Transparent, WkMint.copy(alpha = 0.7f), Color.Transparent))))
        Box(Modifier.weight(1f).fillMaxWidth()) { content() }
    }
}

// ------------------------------------------------------------------ the new side menu
@Composable
fun WkDrawer(version: String, status: Status, onItem: (String) -> Unit) {
    val (stTxt, stCol) = when (status) {
        Status.CONNECTED -> "CONNECTED" to WkOk
        Status.CONNECTING -> "CONNECTING" to WkViolet
        Status.ERROR -> "FAILED" to WkRed
        Status.IDLE -> "NOT CONNECTED" to WkMuted
    }
    val inf = rememberInfiniteTransition(label = "drawer")
    val breathe by inf.animateFloat(0.35f, 1f, infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "b")
    ModalDrawerSheet(drawerContainerColor = WkBg, drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp)) {
        Column(Modifier.verticalScroll(rememberScrollState()).padding(bottom = 18.dp)) {
            // header with glow
            Box(Modifier.fillMaxWidth().drawBehind {
                drawRect(Brush.verticalGradient(listOf(WkMint.copy(alpha = 0.20f), Color.Transparent)))
                val c = Offset(size.width * 0.18f, size.height * 0.35f)
                drawCircle(Brush.radialGradient(listOf(stCol.copy(alpha = 0.30f), Color.Transparent), c, size.width * 0.55f), size.width * 0.55f, c)
            }.padding(start = 20.dp, end = 20.dp, top = 26.dp, bottom = 18.dp)) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painterResource(R.drawable.logo), null,
                            Modifier.size(64.dp).clip(RoundedCornerShape(18.dp)).border(1.5.dp, WkMint.copy(alpha = 0.8f), RoundedCornerShape(18.dp)))
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text("WHITEKID", fontWeight = FontWeight.Black, fontSize = 20.sp, letterSpacing = 5.sp, color = WkText)
                            Text("VPN  •  v$version", color = WkMint, fontSize = 12.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(Modifier.clip(RoundedCornerShape(20.dp)).background(stCol.copy(alpha = 0.14f)).padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(8.dp).clip(CircleShape).background(stCol.copy(alpha = if (status == Status.IDLE) 0.6f else breathe)))
                        Spacer(Modifier.width(8.dp))
                        Text(stTxt, color = stCol, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                    }
                }
            }

            // quick connect hero
            Box(Modifier.padding(horizontal = 16.dp).fillMaxWidth().clip(RoundedCornerShape(20.dp))
                .background(Brush.horizontalGradient(listOf(WkMint, WkMint.copy(alpha = 0.65f))))
                .clickable { onItem("quick") }.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, tint = WkBg, modifier = Modifier.size(26.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("QUICK CONNECT", color = WkBg, fontWeight = FontWeight.Black, letterSpacing = 2.sp, fontSize = 14.sp)
                        Text("Tests every server, joins the fastest", color = WkBg.copy(alpha = 0.8f), fontSize = 11.sp)
                    }
                }
            }

            DrawerHead("EXPLORE")
            DrawerRow(Icons.Default.Settings, "Tools", "Split tunneling, DNS, labs", WkMint) { onItem("tools") }
            DrawerRow(Icons.Default.Info, "About", "Version, protocols, channel", WkViolet) { onItem("about") }
            DrawerRow(Icons.Default.Menu, "Manage servers", "Import, sort, backup, share", Color(0xFF6CC4FF)) { onItem("servers") }
            DrawerRow(Icons.Default.Place, "IP Hunter", "My IP and location", Color(0xFFB7E34D)) { onItem("ip") }
            DrawerRow(Icons.Default.Info, "History and stats", "Sessions and data used", WkOk) { onItem("history") }

            DrawerHead("CUSTOMISE")
            DrawerRow(Icons.Default.Edit, "Themes and appearance", "Accent, dark, AMOLED", Color(0xFFFF6F91)) { onItem("themes") }
            DrawerRow(Icons.Default.Refresh, "Check for update", "", WkViolet) { onItem("update") }
            DrawerRow(Icons.Default.Check, "What's new", "", WkMint) { onItem("new") }
            DrawerRow(Icons.Default.Info, "Help and FAQ", "", Color(0xFF6CC4FF)) { onItem("help") }

            DrawerHead("COMMUNITY")
            DrawerRow(Icons.AutoMirrored.Filled.Send, "Telegram channel", "t.me/whitekidtechzw", Color(0xFF6CC4FF)) { onItem("telegram") }
            DrawerRow(Icons.Default.Share, "Share the app", "", Color(0xFFB7E34D)) { onItem("share") }
            DrawerRow(Icons.Default.Person, "Contact support", "", WkMint) { onItem("support") }
            DrawerRow(Icons.Default.Info, "Open-source licenses", "", WkMuted) { onItem("licenses") }

            Spacer(Modifier.height(10.dp))
            Box(Modifier.padding(horizontal = 16.dp).fillMaxWidth().clip(RoundedCornerShape(16.dp))
                .border(1.dp, WkRed.copy(alpha = 0.55f), RoundedCornerShape(16.dp)).background(WkRed.copy(alpha = 0.08f))
                .clickable { onItem("exit") }.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Close, null, tint = WkRed)
                    Spacer(Modifier.width(12.dp))
                    Text("DISCONNECT AND EXIT", color = WkRed, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun DrawerHead(t: String) {
    Row(Modifier.padding(start = 24.dp, end = 20.dp, top = 20.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(t, color = WkMuted, fontSize = 10.sp, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f).height(1.dp).background(Brush.horizontalGradient(listOf(WkLine, Color.Transparent))))
    }
}

@Composable
private fun DrawerRow(icon: ImageVector, title: String, sub: String, tint: Color, onClick: () -> Unit) {
    Row(Modifier.padding(horizontal = 10.dp).fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick).padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(13.dp)).background(tint.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(21.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = WkText, fontSize = 15.sp, maxLines = 1)
            if (sub.isNotEmpty()) Text(sub, color = WkMuted, fontSize = 11.sp, maxLines = 1)
        }
        Text("›", color = WkMuted, fontSize = 20.sp)
    }
}

// ------------------------------------------------------------------ v1.14.1: one-screen home pieces
/** NETWORK / SERVER card: a small label tag, then a glowing card that opens a dropdown. Optional round "+" button. */
@Composable
fun SelectorCard(
    label: String, title: String, sub: String, badge: String,
    options: List<Pair<String, String>>, onPick: (String) -> Unit, onPlus: (() -> Unit)? = null
) {
    var open by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(18.dp)
    Column {
        Box(Modifier.padding(start = 16.dp).clip(RoundedCornerShape(6.dp)).border(1.dp, WkMint.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)) {
            Text(label, color = WkMint, fontSize = 10.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))
        Box {
            Row(Modifier.fillMaxWidth().clip(shape)
                .background(Brush.horizontalGradient(listOf(WkMint.copy(alpha = 0.16f), WkCard)))
                .border(1.5.dp, Brush.horizontalGradient(listOf(WkMint, WkMint.copy(alpha = 0.25f))), shape)
                .clickable { open = true }.padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(38.dp).clip(RoundedCornerShape(12.dp)).background(WkMint.copy(alpha = 0.2f)), contentAlignment = Alignment.Center) {
                    Text(badge, color = WkMint, fontWeight = FontWeight.Black, fontSize = 13.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold, maxLines = 1, color = WkText, fontSize = 15.sp)
                    if (sub.isNotEmpty()) Text(sub, color = WkMint, fontSize = 11.sp, maxLines = 1)
                }
                if (onPlus != null) {
                    Box(Modifier.size(34.dp).clip(CircleShape).background(WkMint).clickable(onClick = onPlus), contentAlignment = Alignment.Center) {
                        Text("+", color = WkBg, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    }
                } else Text("▾", color = WkMuted, fontSize = 16.sp)
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                options.forEach { (k, l) -> DropdownMenuItem(text = { Text(l, maxLines = 1) }, onClick = { open = false; onPick(k) }) }
            }
        }
    }
}

/** Downloaded / uploaded totals as two small tiles, with the live speed shown while connected. */
@Composable
fun TrafficStrip(down: String, up: String, downSub: String, upSub: String, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf(Triple("▼", down, downSub), Triple("▲", up, upSub)).forEach { (ic, v, sub) ->
            Row(Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(WkCard).border(1.dp, WkLine, RoundedCornerShape(16.dp)).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text(ic, color = WkMint, fontSize = 16.sp)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(v, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1)
                    Text(sub, color = WkMuted, fontSize = 9.sp, letterSpacing = 1.5.sp, maxLines = 1)
                }
            }
        }
    }
}

/** Rounded info pill: duration, ping and protocol side by side. */
@Composable
fun InfoPill(a: Pair<String, String>, b: Pair<String, String>, c: Pair<String, String>) {
    val shape = RoundedCornerShape(26.dp)
    Row(Modifier.fillMaxWidth().clip(shape).border(1.dp, WkMint.copy(alpha = 0.55f), shape).padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        listOf(a, b, c).forEachIndexed { i, (k, v) ->
            if (i > 0) Box(Modifier.width(1.dp).height(30.dp).background(WkLine))
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(k, color = WkMuted, fontSize = 9.sp, letterSpacing = 1.5.sp)
                Text(v, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1)
            }
        }
    }
}
