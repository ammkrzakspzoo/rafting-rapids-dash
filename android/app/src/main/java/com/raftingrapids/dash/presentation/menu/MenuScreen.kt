package com.raftingrapids.dash.presentation.menu

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
import com.raftingrapids.dash.core.navigation.Screen
import com.raftingrapids.dash.core.theme.*
import com.raftingrapids.dash.core.ui.*
import com.raftingrapids.dash.presentation.common.*

@Composable
fun MenuRoute(viewModel: MenuViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    MenuScreen(state, viewModel::play, viewModel::open, viewModel::retry)
}

@Composable
fun MenuScreen(state: MenuUiState, onPlay: () -> Unit, onOpen: (Screen) -> Unit, onRetry: () -> Unit) {
    PageColumn(BackdropTone.Menu) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(R.drawable.logo), null, Modifier.size(40.dp), contentScale = ContentScale.Fit)
            Text(stringResource(R.string.app_name), Modifier.weight(1f).padding(start = 8.dp), style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = { onOpen(Screen.Settings) }, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.settings), style = MaterialTheme.typography.labelSmall) }
        }
        Row(Modifier.fillMaxWidth().heightIn(min = 136.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.expedition), style = MaterialTheme.typography.labelSmall, color = PrimaryDeep)
                Text(stringResource(R.string.menu_title), style = MaterialTheme.typography.headlineMedium, modifier = Modifier.semantics { heading() })
            }
            RaftSprite(state.raft, 116.dp)
        }
        CourseCard(state.course, state.record)
        if (state.finished > 0 && state.gold > 0) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(stringResource(R.string.courses_finished), stringResource(R.string.count_value, state.finished), Modifier.weight(1f))
                StatCard(stringResource(R.string.gold_flags), stringResource(R.string.count_value, state.gold), Modifier.weight(1f))
            }
        } else Text(stringResource(R.string.first_flag), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        if (state.loading) {
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Text(stringResource(R.string.loading_local), style = MaterialTheme.typography.bodyMedium)
        }
        if (state.error) StatusMessage(stringResource(R.string.storage_error), onRetry)
        Text(stringResource(R.string.steering_hint), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        PrimaryButton(stringResource(R.string.play), onPlay, enabled = !state.loading)
        SecondaryButton(stringResource(R.string.courses), { onOpen(Screen.RiverSelect) }, Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SecondaryButton(stringResource(R.string.garage), { onOpen(Screen.RaftGarage) }, Modifier.weight(1f))
            SecondaryButton(stringResource(R.string.challenges), { onOpen(Screen.Challenges) }, Modifier.weight(1f))
        }
        Text(stringResource(R.string.daily_summary, state.dailyComplete), style = MaterialTheme.typography.labelSmall, color = PrimaryDeep)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf(R.drawable.sprite_1, R.drawable.sprite_2, R.drawable.sprite_3, R.drawable.sprite_4, R.drawable.sprite_5).forEach { image ->
                Image(painterResource(image), null, Modifier.size(40.dp), contentScale = ContentScale.Fit)
            }
        }
    }
}
