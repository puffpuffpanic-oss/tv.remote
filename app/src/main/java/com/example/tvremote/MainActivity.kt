package com.example.tvremote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

val Coral = Color(0xFFFF6B6B)
val Amber = Color(0xFFFFB84D)
val Teal = Color(0xFF4ECDC4)
val Blue = Color(0xFF6C8CFF)
val Purple = Color(0xFFB184FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RemoteApp() }
    }
}

@Composable
fun Btn(
    label: String, color: Color, content: Color, modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp), onClick: () -> Unit
) {
    Box(
        modifier.clip(shape).background(color).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { Text(label, color = content, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
}

@Composable
fun RemoteApp() {
    val dark = isSystemInDarkTheme()
    val bg = if (dark) Color(0xFF15131A) else Color(0xFFF6F4EF)
    val surf = if (dark) Color(0xFF221F2A) else Color.White
    val fg = if (dark) Color(0xFFF3F0F8) else Color(0xFF1D1B20)
    val haptic = LocalHapticFeedback.current
    var swipe by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    fun press(code: Int) {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        TvClient.sendKey(code)
    }

    Column(
        Modifier.fillMaxSize().background(bg).systemBarsPadding()
            .verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(TvClient.status, color = fg, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Btn("⏻", Coral, Color.White, Modifier.size(48.dp), CircleShape) { press(26) }
        }

        Row(Modifier.fillMaxWidth().clip(CircleShape).background(surf).padding(4.dp)) {
            Btn("D-pad", if (!swipe) Blue else surf, if (!swipe) Color.White else fg,
                Modifier.weight(1f).height(44.dp), CircleShape) { swipe = false }
            Btn("Swipe", if (swipe) Blue else surf, if (swipe) Color.White else fg,
                Modifier.weight(1f).height(44.dp), CircleShape) { swipe = true }
        }

        if (!swipe) {
            Box(Modifier.size(290.dp).clip(CircleShape).background(surf), contentAlignment = Alignment.Center) {
                Btn("▲", Coral, Color.White, Modifier.align(Alignment.TopCenter).padding(top = 14.dp).size(80.dp), CircleShape) { press(19) }
                Btn("▶", Amber, Color.White, Modifier.align(Alignment.CenterEnd).padding(end = 14.dp).size(80.dp), CircleShape) { press(22) }
                Btn("▼", Teal, Color.White, Modifier.align(Alignment.BottomCenter).padding(bottom = 14.dp).size(80.dp), CircleShape) { press(20) }
                Btn("◀", Blue, Color.White, Modifier.align(Alignment.CenterStart).padding(start = 14.dp).size(80.dp), CircleShape) { press(21) }
                Btn("OK", bg, fg, Modifier.size(90.dp), CircleShape) { press(23) }
            }
        } else {
            var acc by remember { mutableStateOf(Offset.Zero) }
            Box(
                Modifier.size(290.dp).clip(RoundedCornerShape(36.dp))
                    .background(Brush.linearGradient(listOf(Purple, Blue)))
                    .pointerInput(Unit) { detectTapGestures { press(23) } }
                    .pointerInput(Unit) {
                        detectDragGestures(onDragStart = { acc = Offset.Zero }) { change, drag ->
                            change.consume()
                            acc += drag
                            if (abs(acc.x) > 70 || abs(acc.y) > 70) {
                                press(
                                    if (abs(acc.x) > abs(acc.y)) (if (acc.x > 0) 22 else 21)
                                    else (if (acc.y > 0) 20 else 19)
                                )
                                acc = Offset.Zero
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) { Text("Swipe to move\nTap to select", color = Color.White, fontWeight = FontWeight.SemiBold) }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Btn("← Back", Amber, Color(0xFF2A1D00), Modifier.weight(1f).height(52.dp)) { press(4) }
            Btn("⌂ Home", Teal, Color(0xFF06302D), Modifier.weight(1f).height(52.dp)) { press(3) }
            Btn("☰ Menu", Purple, Color.White, Modifier.weight(1f).height(52.dp)) { press(82) }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Btn("−", surf, fg, Modifier.weight(1f).height(56.dp)) { press(25) }
            Btn("Mute", Coral, Color.White, Modifier.weight(1f).height(56.dp)) { press(164) }
            Btn("+", surf, fg, Modifier.weight(1f).height(56.dp)) { press(24) }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = text, onValueChange = { text = it },
                modifier = Modifier.weight(1f), singleLine = true,
                placeholder = { Text("Type on TV…") },
                textStyle = TextStyle(color = fg),
                shape = RoundedCornerShape(22.dp)
            )
            Btn("Send", Blue, Color.White, Modifier.height(56.dp).width(80.dp)) {
                if (text.isNotEmpty()) { TvClient.sendText(text); text = "" }
            }
        }
    }
}
