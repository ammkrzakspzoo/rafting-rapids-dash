package com.raftingrapids.dash.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.raftingrapids.dash.core.theme.*
import com.raftingrapids.dash.domain.model.Flag
import com.raftingrapids.dash.presentation.common.labelRes

@Composable
fun FlagBadge(flag: Flag, size: Dp = 32.dp) {
    val description = stringResource(flag.labelRes())
    val color = when (flag) { Flag.Gold -> Accent; Flag.Silver -> Secondary; Flag.Bronze -> AccentLight; Flag.None -> Rock.copy(alpha = 0.4f) }
    Canvas(Modifier.size(size).semantics { contentDescription = description }) {
        drawLine(PrimaryDeep, Offset(this.size.width * 0.25f, this.size.height * 0.12f), Offset(this.size.width * 0.25f, this.size.height * 0.9f), strokeWidth = this.size.width * 0.06f)
        drawPath(Path().apply { moveTo(this@Canvas.size.width * 0.28f, this@Canvas.size.height * 0.15f); lineTo(this@Canvas.size.width * 0.9f, this@Canvas.size.height * 0.22f); lineTo(this@Canvas.size.width * 0.72f, this@Canvas.size.height * 0.43f); lineTo(this@Canvas.size.width * 0.9f, this@Canvas.size.height * 0.6f); lineTo(this@Canvas.size.width * 0.28f, this@Canvas.size.height * 0.55f); close() }, color)
    }
}
