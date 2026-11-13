package com.raftingrapids.dash.presentation.raftgarage

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raftingrapids.dash.R
import com.raftingrapids.dash.core.theme.*
import com.raftingrapids.dash.core.ui.*
import com.raftingrapids.dash.domain.model.*
import com.raftingrapids.dash.presentation.common.*

@Composable
fun RaftGarageRoute(viewModel: RaftGarageViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    RaftGarageScreen(state, onBack, viewModel::focus, viewModel::select, viewModel::retry)
}

@Composable
fun RaftGarageScreen(state: RaftGarageUiState, onBack: () -> Unit, onFocus: (RaftDesign) -> Unit, onSelect: () -> Unit, onRetry: () -> Unit) {
    val unlocked = state.focused.isUnlocked(state.progress)
    val selected = state.focused == state.progress.selectedRaft
    PageColumn {
        ScreenHeader(stringResource(R.string.garage_title), onBack)
        Crossfade(state.focused, label = "raft") { raft ->
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                RaftSprite(raft, 156.dp)
            }
        }
        Text(stringResource(R.string.cosmetic_hint), style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        state.rafts.chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEach { raft ->
                    val status = stringResource(if (raft == state.progress.selectedRaft) R.string.selected else if (raft.isUnlocked(state.progress)) R.string.unlocked else R.string.locked)
                    RiverCard(Modifier.weight(1f).semantics { stateDescription = status }, state.focused == raft) {
                        RaftSprite(raft, 64.dp, Modifier.align(Alignment.CenterHorizontally))
                        Text(stringResource(raft.nameRes()), style = MaterialTheme.typography.titleMedium)
                        Text(status, style = MaterialTheme.typography.labelSmall)
                        TextButton(onClick = { onFocus(raft) }, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.select)) }
                    }
                }
            }
        }
        if (!unlocked) Text(when (state.focused) {
            RaftDesign.Lagoon -> stringResource(R.string.raft_unlock_finishes, (5 - state.progress.records.size).coerceAtLeast(0))
            RaftDesign.Glacier -> stringResource(R.string.raft_unlock_gold, (10 - state.progress.goldCount).coerceAtLeast(0))
            else -> stringResource(R.string.raft_unlock_gold, (40 - state.progress.goldCount).coerceAtLeast(0))
        }, style = MaterialTheme.typography.bodyMedium)
        if (state.progress.error) StatusMessage(stringResource(R.string.storage_error), onRetry)
        PrimaryButton(stringResource(if (selected) R.string.selected else R.string.select_raft), onSelect, enabled = unlocked && !selected && !state.progress.loading)
    }
}
