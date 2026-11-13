package com.raftingrapids.dash.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.raftingrapids.dash.core.theme.*

@Composable
fun RiverCard(modifier: Modifier = Modifier, selected: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier = modifier, shape = RoundedCornerShape(20.dp), color = Foam.copy(alpha = 0.92f), contentColor = Ink,
        shadowElevation = 4.dp, border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) Accent else Foam.copy(alpha = 0.75f))) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}
