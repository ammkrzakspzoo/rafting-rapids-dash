package com.raftingrapids.dash.core.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val OceanFresh = lightColorScheme(
    primary = PrimaryDeep, onPrimary = Foam, primaryContainer = SurfaceWater, onPrimaryContainer = Ink,
    secondary = Primary, onSecondary = Ink, secondaryContainer = WaterMid,
    tertiary = Accent, onTertiary = Ink, background = Background, onBackground = Ink,
    surface = Foam, onSurface = Ink, surfaceVariant = SurfaceWater, onSurfaceVariant = TextSecondary,
    outline = Rock, error = Error, onError = Foam
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = OceanFresh, typography = RiverTypography, content = content)
}
