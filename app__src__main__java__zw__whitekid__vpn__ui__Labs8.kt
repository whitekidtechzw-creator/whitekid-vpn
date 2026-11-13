package zw.whitekid.vpn.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import zw.whitekid.vpn.R
import zw.whitekid.vpn.data.Store

// ------------------------------------------------------------------ Vision 2: intro splash
/** Glowing intro shown once each time the app starts. Blocks touches while visible, then fades out and leaves composition. */
@Composable
fun SplashOverlay() {
    var show by rememberSaveable { mutableStateOf(true) }
    LaunchedEffect(Unit) { delay(1600); show = false }
    val a by animateFloatAsState(if (show) 1f else 0f, tween(500), label = "splashAlpha")
    if (show || a > 0.01f) SplashBody(a)
}

@Composable
private fun SplashBody(a: Float) {
    val inf = rememberInfiniteTransition(label = "splash")
    val glow by inf.animateFloat(0.5f, 1f, infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "g")
    val prog = remember { Animatable(0f) }
    LaunchedEffect(Unit) { prog.animateTo(1f, tween(1400, easing = FastOutSlowInEasing)) }
    Box(
        Modifier.fillMaxSize().alpha(a).background(WkBg).pointerInput(Unit) { detectTapGestures { } },
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier.size(320.dp).drawBehind {
            val rr = size.minDimension / 2
            drawCircle(Brush.radialGradient(listOf(WkMint.copy(alpha = 0.40f * glow), Color.Transparent), center, rr), rr, center)
        })
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painterResource(R.drawable.logo), null,
                Modifier.size(116.dp).clip(RoundedCornerShape(30.dp)).border(1.5.dp, WkMint.copy(alpha = 0.8f), RoundedCornerShape(30.dp)))
            Spacer(Modifier.height(20.dp))
            Text("WHITEKID", color = WkText, fontSize = 30.sp, fontWeight = FontWeight.Black, letterSpacing = 8.sp)
            Spacer(Modifier.height(10.dp))
            Box(Modifier.clip(RoundedCornerShape(20.dp)).background(WkMint).padding(horizontal = 14.dp, vertical = 4.dp)) {
                Text("VISION 2", color = WkBg, fontSize = 12.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
            }
            Spacer(Modifier.height(28.dp))
            Box(Modifier.width(150.dp).height(3.dp).clip(RoundedCornerShape(2.dp)).background(WkLine)) {
                Box(Modifier.fillMaxHeight().fillMaxWidth(prog.value).background(WkMint))
            }
        }
    }
}

// ------------------------------------------------------------------ Vision 2: quick actions row on Connect
@Composable
fun QuickActions(onQuick: (String) -> Unit) {
    val items: List<Triple<String, ImageVector, String>> = listOf(
        Triple("MY IP", Icons.Default.Place, "ip"),
        Triple("FASTEST", Icons.Default.Star, "quick"),
        Triple("INSIGHTS", Icons.Default.DateRange, "insights"),
        Triple("DOCTOR", Icons.Default.Build, "doctor")
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { (label, icon, key) ->
            Column(
                Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(WkCard)
                    .border(1.dp, WkLine, RoundedCornerShape(16.dp)).clickable { onQuick(key) }.padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(icon, null, tint = WkMint, modifier = Modifier.size(20.dp))
                Spacer(Modifier.height(4.dp))
                Text(label, fontSize = 9.sp, letterSpacing = 1.sp, fontWeight = FontWeight.Bold, color = WkMuted, maxLines = 1)
            }
        }
    }
}

// ------------------------------------------------------------------ Vision 2: insights popup (usage, streak, 7-day chart)
@Composable
fun InsightsDialog(onClose: () -> Unit) {
    val days = remember { (6 downTo 0).map { Store.usedDaysAgo(it) } }
    val labels = remember {
        val f = java.text.SimpleDateFormat("EEE", java.util.Locale.getDefault())
        (6 downTo 0).map { n ->
            val c = java.util.Calendar.getInstance(); c.add(java.util.Calendar.DAY_OF_YEAR, -n)
            f.format(c.time).take(3).uppercase()
        }
    }
    val streak = remember {
        var i = if (Store.usedDaysAgo(0) > 0) 0 else 1
        var s = 0
        while (i < 365 && Store.usedDaysAgo(i) > 0) { s++; i++ }
        s
    }
    val maxV = (days.maxOrNull() ?: 0L).coerceAtLeast(1L)
    PopupScreen("Insights", onClose) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Stat("TODAY", bytes(days.last()), Modifier.weight(1f))
                Stat("THIS MONTH", bytes(Store.usedMonth()), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Stat("7 DAYS", bytes(days.sum()), Modifier.weight(1f))
                Stat("ALL TIME", bytes(Store.totalRx + Store.totalTx), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Stat("SESSIONS", "${Store.sessions}", Modifier.weight(1f))
                Stat("LONGEST", fmtDur(Store.longestSec), Modifier.weight(1f))
            }
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                .background(Brush.horizontalGradient(listOf(WkMint.copy(alpha = 0.22f), WkCard)))
                .border(1.dp, WkMint.copy(alpha = 0.6f), RoundedCornerShape(16.dp)).padding(14.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("🔥", fontSize = 28.sp)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(if (streak == 0) "No streak yet" else "$streak day" + (if (streak > 1) "s" else "") + " in a row", fontWeight = FontWeight.Bold)
                    Text("Days you used the VPN back to back", color = WkMuted, fontSize = 12.sp)
                }
            }
            Text("LAST 7 DAYS", color = WkMint, fontWeight = FontWeight.Black, fontSize = 11.sp, letterSpacing = 3.sp)
            Row(Modifier.fillMaxWidth().height(160.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
                days.forEachIndexed { i, v ->
                    val frac = (v.toFloat() / maxV).coerceIn(0.02f, 1f)
                    val h by animateFloatAsState(frac, tween(700, delayMillis = i * 60), label = "bar")
                    val c = if (i == days.lastIndex) WkMint else WkMint.copy(alpha = 0.55f)
                    Column(Modifier.weight(1f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom) {
                        Text(if (v > 0) bytes(v) else "", fontSize = 8.sp, color = WkMuted, maxLines = 1, softWrap = false)
                        Box(Modifier.fillMaxWidth().fillMaxHeight(h * 0.8f).clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(Brush.verticalGradient(listOf(c, c.copy(alpha = 0.3f)))))
                    }
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                labels.forEach { Text(it, Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 10.sp, color = WkMuted) }
            }
        }
    }
}
