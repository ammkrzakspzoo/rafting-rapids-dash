package com.raftingrapids.dash.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import com.raftingrapids.dash.R
import com.raftingrapids.dash.core.theme.Error

@Composable
fun StatusMessage(message: String, onRetry: (() -> Unit)? = null) {
    RiverCard(Modifier.fillMaxWidth().semantics { liveRegion = LiveRegionMode.Polite; stateDescription = message }) {
        Text(message, style = MaterialTheme.typography.bodyMedium, color = if (onRetry != null) Error else MaterialTheme.colorScheme.onSurface)
        if (onRetry != null) SecondaryButton(stringResource(R.string.retry), onRetry)
    }
}
