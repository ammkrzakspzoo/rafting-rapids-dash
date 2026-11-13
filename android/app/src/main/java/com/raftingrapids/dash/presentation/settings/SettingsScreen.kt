package com.raftingrapids.dash.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raftingrapids.dash.R
import com.raftingrapids.dash.core.theme.*
import com.raftingrapids.dash.core.ui.*
import com.raftingrapids.dash.domain.model.*
import com.raftingrapids.dash.presentation.dialog.ResetProgressDialog
import kotlin.math.roundToInt

@Composable
fun SettingsRoute(viewModel: SettingsViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(state, onBack, viewModel::change, viewModel::requestReset, viewModel::retry)
    if (state.resetVisible) ResetProgressDialog(state.saving, viewModel::dismissReset, viewModel::reset)
}

@Composable
fun SettingsScreen(state: SettingsUiState, onBack: () -> Unit, onChange: ((ControlSettings) -> ControlSettings) -> Unit, onReset: () -> Unit, onRetry: () -> Unit) {
    PageColumn {
        ScreenHeader(stringResource(R.string.settings), onBack)
        RiverCard(Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.controls), style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SecondaryButton(stringResource(R.string.drag), { onChange { it.copy(mode = ControlMode.Drag) } }, Modifier.weight(1f), enabled = !state.saving, selected = state.settings.mode == ControlMode.Drag)
                SecondaryButton(stringResource(R.string.tilt), { onChange { it.copy(mode = ControlMode.Tilt) } }, Modifier.weight(1f), enabled = state.tiltAvailable && !state.saving, selected = state.settings.mode == ControlMode.Tilt)
            }
            if (!state.tiltAvailable) Text(stringResource(R.string.tilt_unavailable), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            val sensitivity = stringResource(listOf(R.string.low, R.string.standard, R.string.high)[state.settings.sensitivity])
            Text(stringResource(R.string.sensitivity, sensitivity), style = MaterialTheme.typography.bodyLarge)
            Slider(value = state.settings.sensitivity.toFloat(), onValueChange = { value -> onChange { it.copy(sensitivity = value.roundToInt()) } }, enabled = !state.saving, valueRange = 0f..2f, steps = 1, modifier = Modifier.heightIn(min = 48.dp).semantics { stateDescription = sensitivity })
            Text(stringResource(R.string.controls_hint), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
        RiverCard(Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.comfort), style = MaterialTheme.typography.titleLarge)
            SettingSwitch(stringResource(R.string.sound), state.settings.sound, !state.saving) { value -> onChange { it.copy(sound = value) } }
            SettingSwitch(stringResource(R.string.haptics), state.settings.haptics, !state.saving) { value -> onChange { it.copy(haptics = value) } }
            SettingSwitch(stringResource(R.string.reduce_motion), state.settings.reducedMotion, !state.saving) { value -> onChange { it.copy(reducedMotion = value) } }
        }
        RiverCard(Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.local_progress), style = MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.progress_summary, state.finished, state.gold), style = MaterialTheme.typography.bodyMedium)
            SecondaryButton(stringResource(R.string.reset_progress), onReset, Modifier.fillMaxWidth(), enabled = !state.saving)
        }
        if (state.saving) LinearProgressIndicator(Modifier.fillMaxWidth())
        if (state.error) StatusMessage(stringResource(R.string.storage_error), onRetry)
        Text(stringResource(R.string.privacy), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

@Composable
private fun SettingSwitch(label: String, checked: Boolean, enabled: Boolean, onChange: (Boolean) -> Unit) {
    val description = stringResource(if (checked) R.string.enabled else R.string.disabled)
    Row(Modifier.fillMaxWidth().heightIn(min = 56.dp).semantics { stateDescription = description }, verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
        Switch(checked, onCheckedChange = onChange, enabled = enabled)
    }
}
