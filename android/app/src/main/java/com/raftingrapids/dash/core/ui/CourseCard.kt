package com.raftingrapids.dash.core.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.raftingrapids.dash.R
import com.raftingrapids.dash.core.theme.*
import com.raftingrapids.dash.domain.model.*
import com.raftingrapids.dash.presentation.common.*

@Composable
fun CourseCard(course: Course, record: CourseRecord?, unlocked: Boolean = true, selected: Boolean = false, onSelect: (() -> Unit)? = null) {
    val status = stringResource(if (!unlocked) R.string.locked else if (selected) R.string.selected else R.string.unlocked)
    RiverCard(Modifier.fillMaxWidth().semantics { stateDescription = status }, selected) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(stringResource(R.string.course_number, course.id), style = MaterialTheme.typography.labelSmall, color = PrimaryDeep)
                Text(stringResource(course.nameRes()), style = MaterialTheme.typography.titleLarge)
                Text(stringResource(R.string.chapter_detail, stringResource(chapterRes(course.chapter)), stringResource(difficultyRes(course.chapter))), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
            FlagBadge(record?.flag ?: Flag.None)
        }
        Text(if (!unlocked) stringResource(R.string.unlock_course, course.id - 1) else record?.let { stringResource(R.string.best_time, it.bestMillis / 1000.0) } ?: stringResource(R.string.no_record), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        if (onSelect != null) SecondaryButton(stringResource(if (selected) R.string.selected else R.string.select), onSelect, Modifier.fillMaxWidth(), selected = selected)
    }
}
