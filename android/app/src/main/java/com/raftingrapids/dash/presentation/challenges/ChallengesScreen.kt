package com.raftingrapids.dash.presentation.challenges

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raftingrapids.dash.R
import com.raftingrapids.dash.core.theme.*
import com.raftingrapids.dash.core.ui.*
import com.raftingrapids.dash.presentation.common.*

@Composable
fun ChallengesRoute(viewModel: ChallengesViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ChallengesScreen(state, onBack, viewModel::play, viewModel::retry)
}

@Composable
fun ChallengesScreen(state: ChallengesUiState, onBack: () -> Unit, onPlay: () -> Unit, onRetry: () -> Unit) {
    PageColumn {
        ScreenHeader(stringResource(R.string.daily_title), onBack)
        Text(stringResource(R.string.date_value, state.date), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        CourseCard(state.course, state.progress.records[state.course.id])
        val goal = state.goal
        if (goal != null) {
            if (goal.completed) StatusMessage(stringResource(R.string.goals_complete))
            else if (goal.finishes == 0 && goal.paddles == 0 && goal.boosts == 0) Text(stringResource(R.string.goals_empty), style = MaterialTheme.typography.bodyMedium)
            GoalCard(stringResource(R.string.goal_finish, stringResource(state.course.nameRes())), goal.finishes.coerceAtMost(1), 1)
            GoalCard(stringResource(R.string.goal_paddles, goal.paddleTarget), goal.paddles, goal.paddleTarget)
            GoalCard(stringResource(R.string.goal_boost, goal.boostTarget), goal.boosts, goal.boostTarget)
        } else if (!state.progress.error) LinearProgressIndicator(Modifier.fillMaxWidth())
        if (state.progress.error) StatusMessage(stringResource(R.string.storage_error), onRetry)
        Text(stringResource(R.string.badges, state.progress.dailyBadges), style = MaterialTheme.typography.bodyMedium)
        Text(stringResource(R.string.daily_hint), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        PrimaryButton(stringResource(R.string.play), onPlay, enabled = !state.progress.loading)
    }
}

@Composable
private fun GoalCard(title: String, count: Int, target: Int) {
    val summary = stringResource(R.string.goal_progress, count.coerceAtMost(target), target)
    val status = stringResource(if (count >= target) R.string.complete else R.string.in_progress)
    RiverCard(Modifier.fillMaxWidth().semantics { stateDescription = summary; liveRegion = LiveRegionMode.Polite }, count >= target) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Text(summary, style = MaterialTheme.typography.bodyMedium)
        LinearProgressIndicator(progress = { (count.toFloat() / target).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(6.dp), color = Primary, trackColor = SurfaceWater)
        Text(status, style = MaterialTheme.typography.labelSmall, color = PrimaryDeep)
    }
}
