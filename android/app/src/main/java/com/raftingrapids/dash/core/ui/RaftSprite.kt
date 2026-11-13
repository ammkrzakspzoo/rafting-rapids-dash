package com.raftingrapids.dash.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.Dp
import com.raftingrapids.dash.R
import com.raftingrapids.dash.core.theme.*
import com.raftingrapids.dash.domain.model.RaftDesign
import com.raftingrapids.dash.presentation.common.*

@Composable
fun RaftSprite(raft: RaftDesign, size: Dp, modifier: Modifier = Modifier) {
    val description = stringResource(R.string.raft_description, stringResource(raft.nameRes()))
    val body = when (raft) { RaftDesign.Sunrise -> Accent; RaftDesign.Lagoon -> Primary; RaftDesign.Glacier -> Secondary; RaftDesign.Sunburst -> AccentLight }
    Box(modifier.size(size).semantics { contentDescription = description }, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height
            drawOval(Ink.copy(alpha = 0.2f), Offset(w * 0.11f, h * 0.08f), Size(w * 0.8f, h * 0.9f))
            drawLine(PrimaryDeep, Offset(w * 0.04f, h * 0.2f), Offset(w * 0.93f, h * 0.85f), w * 0.04f)
            drawLine(PrimaryDeep, Offset(w * 0.96f, h * 0.2f), Offset(w * 0.07f, h * 0.85f), w * 0.04f)
            drawOval(Brush.linearGradient(listOf(Foam, body)), Offset(w * 0.08f, h * 0.02f), Size(w * 0.84f, h * 0.93f))
            drawOval(body, Offset(w * 0.13f, h * 0.06f), Size(w * 0.74f, h * 0.86f))
            drawOval(PrimaryDeep, Offset(w * 0.27f, h * 0.19f), Size(w * 0.46f, h * 0.61f))
            drawOval(SurfaceWater, Offset(w * 0.3f, h * 0.22f), Size(w * 0.4f, h * 0.55f))
            drawLine(body, Offset(w * 0.25f, h * 0.34f), Offset(w * 0.75f, h * 0.34f), w * 0.06f)
            drawLine(body, Offset(w * 0.25f, h * 0.65f), Offset(w * 0.75f, h * 0.65f), w * 0.06f)
        }
        Image(painterResource(raft.imageRes()), null, Modifier.size(size * 0.32f), contentScale = ContentScale.Fit)
    }
}
