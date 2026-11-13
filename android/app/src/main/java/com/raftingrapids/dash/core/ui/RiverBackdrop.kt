package com.raftingrapids.dash.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.raftingrapids.dash.R
import com.raftingrapids.dash.core.theme.*

enum class BackdropTone { Splash, Menu, Game, Result, Quiet }

@Composable
fun RiverBackdrop(tone: BackdropTone, content: @Composable BoxScope.() -> Unit) {
    val colors = when (tone) {
        BackdropTone.Splash -> listOf(PrimaryDeep.copy(alpha = 0.96f), PrimaryDeep.copy(alpha = 0.87f), Ink.copy(alpha = 0.91f))
        BackdropTone.Menu -> listOf(Background.copy(alpha = 0.96f), Background.copy(alpha = 0.86f), WaterMid.copy(alpha = 0.82f))
        BackdropTone.Game -> listOf(PrimaryDeep.copy(alpha = 0.44f), Primary.copy(alpha = 0.32f), PrimaryDeep.copy(alpha = 0.72f))
        BackdropTone.Result -> listOf(Background.copy(alpha = 0.96f), SurfaceWater.copy(alpha = 0.94f), WaterMid.copy(alpha = 0.90f))
        BackdropTone.Quiet -> listOf(Background.copy(alpha = 0.97f), SurfaceWater.copy(alpha = 0.94f))
    }
    Box(Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.bg_game), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(colors)))
        Canvas(Modifier.fillMaxSize()) {
            repeat(4) { index ->
                val x = size.width * (index * 0.32f - 0.3f)
                val path = Path().apply {
                    moveTo(x, 0f)
                    cubicTo(x + size.width, size.height * 0.35f, x - size.width * 0.3f, size.height * 0.65f, x + size.width * 0.5f, size.height)
                }
                drawPath(path, Foam.copy(alpha = if (tone == BackdropTone.Splash) 0.12f else 0.18f), style = Stroke(width = 2.dp.toPx()))
            }
        }
        content()
    }
}

@Composable
fun PageColumn(tone: BackdropTone = BackdropTone.Quiet, content: @Composable ColumnScope.() -> Unit) {
    RiverBackdrop(tone) {
        Column(Modifier.align(Alignment.TopCenter).safeDrawingPadding().widthIn(max = 480.dp).fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp), content = content)
    }
}
