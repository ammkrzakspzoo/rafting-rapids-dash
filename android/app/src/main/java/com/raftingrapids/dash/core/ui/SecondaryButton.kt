package com.raftingrapids.dash.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.raftingrapids.dash.core.theme.*

@Composable
fun SecondaryButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, selected: Boolean = false) {
    OutlinedButton(onClick = onClick, enabled = enabled, modifier = modifier.heightIn(min = 48.dp).semantics { this.selected = selected }, shape = RoundedCornerShape(14.dp),
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (enabled) PrimaryDeep.copy(alpha = 0.4f) else Rock.copy(alpha = 0.3f)),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) AccentLight.copy(alpha = 0.42f) else Foam.copy(alpha = 0.72f), contentColor = PrimaryDeep, disabledContainerColor = Secondary.copy(alpha = 0.56f), disabledContentColor = TextSecondary),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
}
