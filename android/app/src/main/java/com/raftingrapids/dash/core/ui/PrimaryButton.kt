package com.raftingrapids.dash.core.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.raftingrapids.dash.core.theme.*

@Composable
fun PrimaryButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val interactions = remember { MutableInteractionSource() }
    val pressed by interactions.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.98f else 1f, spring(dampingRatio = 0.75f, stiffness = 500f), label = "button")
    val shape = RoundedCornerShape(18.dp)
    Button(onClick = onClick, enabled = enabled, interactionSource = interactions,
        modifier = modifier.fillMaxWidth().heightIn(min = 60.dp).graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(if (enabled) 6.dp else 0.dp, shape)
            .background(Brush.horizontalGradient(if (enabled) listOf(AccentLight, Accent) else listOf(Secondary.copy(alpha = 0.56f), Secondary.copy(alpha = 0.56f))), shape),
        shape = shape, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Ink, disabledContainerColor = Color.Transparent, disabledContentColor = TextSecondary),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge)
    }
}
