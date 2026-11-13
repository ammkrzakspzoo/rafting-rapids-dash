package com.raftingrapids.dash.presentation.riverselect

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raftingrapids.dash.R
import com.raftingrapids.dash.core.theme.*
import com.raftingrapids.dash.core.ui.*
import com.raftingrapids.dash.presentation.common.*

@Composable
fun RiverSelectRoute(viewModel: RiverSelectViewModel, onBack: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    RiverSelectScreen(state, onBack, viewModel::chapter, viewModel::select, viewModel::play, viewModel::retry)
}

@Composable
fun RiverSelectScreen(state: RiverSelectUiState, onBack: () -> Unit, onChapter: (Int) -> Unit, onSelect: (Int) -> Unit, onPlay: () -> Unit, onRetry: () -> Unit) {
    PageColumn {
        ScreenHeader(stringResource(R.string.river_select), onBack)
        Text(stringResource(R.string.course_total), style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(5) { chapter -> SecondaryButton(stringResource(chapterRes(chapter)), { onChapter(chapter) }, selected = state.chapter == chapter) }
        }
        val selected = state.courses.getOrNull(state.selectedCourse - 1)
        if (selected != null) {
            RiverCard(Modifier.fillMaxWidth(), selected = true) {
                Text(stringResource(selected.nameRes()), style = MaterialTheme.typography.titleMedium)
                Text(stringResource(R.string.gold_target, selected.goldMillis / 1000.0, selected.goldPaddles), style = MaterialTheme.typography.bodyMedium)
                if (selected.id > state.progress.unlockedThrough) Text(stringResource(R.string.unlock_course, selected.id - 1), color = TextSecondary)
                PrimaryButton(stringResource(R.string.play), onPlay, enabled = !state.progress.loading && selected.id <= state.progress.unlockedThrough)
            }
        }
        if (state.progress.error) StatusMessage(stringResource(R.string.storage_error), onRetry)
        state.courses.filter { it.chapter == state.chapter }.forEach { course ->
            CourseCard(course, state.progress.records[course.id], course.id <= state.progress.unlockedThrough, course.id == state.selectedCourse, { onSelect(course.id) })
        }
    }
}
