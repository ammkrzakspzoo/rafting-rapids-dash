package com.raftingrapids.dash.core.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.*
import com.raftingrapids.dash.core.theme.*

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    RiverCard(modifier.semantics { liveRegion = LiveRegionMode.Polite; stateDescription = value }) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = PrimaryDeep)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}
