package com.raftingrapids.dash.presentation.gameover

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raftingrapids.dash.R
import com.raftingrapids.dash.core.theme.*
import com.raftingrapids.dash.core.ui.*
import com.raftingrapids.dash.domain.model.*
import com.raftingrapids.dash.presentation.common.*

@Composable
fun GameOverRoute(viewModel: GameOverViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    GameOverScreen(state, viewModel::replay, viewModel::next, viewModel::menu, viewModel::retry)
}

@Composable
fun GameOverScreen(state: GameOverUiState, onReplay: () -> Unit, onNext: () -> Unit, onMenu: () -> Unit, onRetry: () -> Unit) {
    val result = state.result ?: return
    val title = stringResource(if (state.goldCount == 40) R.string.all_mastered else if (result.flag == Flag.Gold) R.string.gold_earned else if (result.completed) R.string.course_complete else R.string.run_ended)
    PageColumn(BackdropTone.Result) {
        Column(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(stringResource(R.string.course_number, result.course.id), style = MaterialTheme.typography.labelSmall, color = PrimaryDeep)
            if (result.completed) FlagBadge(result.flag, 104.dp) else Image(painterResource(R.drawable.logo), null, Modifier.size(112.dp), contentScale = ContentScale.Fit)
            Text(title, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, modifier = Modifier.semantics { heading(); liveRegion = LiveRegionMode.Polite })
            Text(stringResource(result.course.nameRes()), style = MaterialTheme.typography.titleMedium, color = PrimaryDeep)
        }
        if (result.elapsedMillis > 0 && result.paddles > 0) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(stringResource(R.string.time_label), stringResource(R.string.time_value, result.elapsedMillis / 1000.0), Modifier.weight(1f))
                StatCard(stringResource(R.string.paddles_label), stringResource(R.string.count_value, result.paddles), Modifier.weight(1f))
            }
        }
        RiverCard(Modifier.fillMaxWidth()) {
            if (result.paddles == 0 || result.elapsedMillis == 0L) Text(stringResource(R.string.plain_metrics, result.elapsedMillis / 1000.0, result.paddles), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.distance_value, (result.distance / result.course.length * 100).toInt()), style = MaterialTheme.typography.bodyMedium)
            Text(stringResource(R.string.gold_target, result.course.goldMillis / 1000.0, result.course.goldPaddles), style = MaterialTheme.typography.bodyMedium)
            Text(stringResource(R.string.boost_collision_metrics, result.boosts, result.collisions), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            if (state.newBest) Text(stringResource(R.string.personal_best), style = MaterialTheme.typography.labelLarge, color = PrimaryDeep)
            if (result.completed && result.flag != Flag.Gold) {
                val time = ((result.elapsedMillis - result.course.goldMillis).coerceAtLeast(0) + 99) / 100 * 0.1
                val paddles = (result.course.goldPaddles - result.paddles).coerceAtLeast(0)
                Text(when {
                    time > 0 && paddles > 0 -> stringResource(R.string.both_gold, time, paddles)
                    time > 0 -> stringResource(R.string.faster_gold, time)
                    else -> stringResource(R.string.more_paddles, paddles)
                }, style = MaterialTheme.typography.bodyMedium)
            }
        }
        when (state.saveStatus) {
            SaveStatus.Saving -> { LinearProgressIndicator(Modifier.fillMaxWidth()); Text(stringResource(R.string.saving), style = MaterialTheme.typography.bodyMedium) }
            SaveStatus.Error -> StatusMessage(stringResource(R.string.save_failed), onRetry)
            SaveStatus.Saved -> Text(stringResource(R.string.saved), style = MaterialTheme.typography.labelSmall, color = PrimaryDeep)
        }
        if (state.nextAvailable && state.saveStatus == SaveStatus.Saved) Text(stringResource(R.string.next_unlocked), style = MaterialTheme.typography.bodyMedium)
        Text(stringResource(if (result.completed) R.string.complete_hint else R.string.retired_hint), style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        PrimaryButton(stringResource(R.string.play_again), onReplay)
        if (state.nextAvailable) SecondaryButton(stringResource(R.string.next_course), onNext, Modifier.fillMaxWidth())
        SecondaryButton(stringResource(R.string.menu), onMenu, Modifier.fillMaxWidth())
    }
}
