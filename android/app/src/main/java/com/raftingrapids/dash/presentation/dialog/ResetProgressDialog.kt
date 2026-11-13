package com.raftingrapids.dash.presentation.dialog

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.raftingrapids.dash.R

@Composable
fun ResetProgressDialog(saving: Boolean, onDismiss: () -> Unit, onReset: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss, modifier = Modifier.safeDrawingPadding(),
        title = { Text(stringResource(R.string.reset_title)) }, text = { Text(stringResource(R.string.reset_message)) },
        confirmButton = { TextButton(onClick = onReset, enabled = !saving) { Text(stringResource(R.string.reset)) } },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !saving) { Text(stringResource(R.string.cancel)) } })
}
