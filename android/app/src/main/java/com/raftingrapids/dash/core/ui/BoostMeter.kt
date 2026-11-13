package com.raftingrapids.dash.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.raftingrapids.dash.R
import com.raftingrapids.dash.core.theme.*

@Composable
fun BoostMeter(charge: Double, active: Boolean) {
    val label = stringResource(if (active) R.string.boost_active else if (charge >= 99.99) R.string.boost_ready else R.string.boost_hint)
    Column(Modifier.semantics { stateDescription = label }, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(R.string.boost_charge, charge.toInt()), style = MaterialTheme.typography.labelSmall)
            Text(stringResource(if (active) R.string.boosting else R.string.boost), style = MaterialTheme.typography.labelSmall, color = PrimaryDeep)
        }
        LinearProgressIndicator(progress = { (charge / 100.0).toFloat().coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(6.dp), color = Accent, trackColor = SurfaceWater)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}
