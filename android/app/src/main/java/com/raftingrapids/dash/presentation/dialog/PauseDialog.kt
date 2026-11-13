package com.raftingrapids.dash.presentation.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.raftingrapids.dash.R
import com.raftingrapids.dash.core.ui.*

@Composable
fun PauseDialog(elapsed: Long, paddles: Int, onDismiss: () -> Unit, onResume: () -> Unit, onReplay: () -> Unit, onSettings: () -> Unit, onMenu: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        RiverCard(Modifier.safeDrawingPadding().verticalScroll(rememberScrollState())) {
            Text(stringResource(R.string.paused_title), style = MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.pause_summary, elapsed / 1000.0, paddles), style = MaterialTheme.typography.bodyMedium)
            Text(stringResource(R.string.paused_hint), style = MaterialTheme.typography.bodyMedium)
            PrimaryButton(stringResource(R.string.resume), onResume)
            SecondaryButton(stringResource(R.string.play_again), onReplay, Modifier.fillMaxWidth())
            SecondaryButton(stringResource(R.string.settings), onSettings, Modifier.fillMaxWidth())
            SecondaryButton(stringResource(R.string.menu), onMenu, Modifier.fillMaxWidth())
        }
    }
}
