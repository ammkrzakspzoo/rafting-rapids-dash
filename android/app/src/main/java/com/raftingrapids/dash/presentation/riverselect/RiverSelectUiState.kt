package com.raftingrapids.dash.presentation.riverselect

import com.raftingrapids.dash.domain.model.*

data class RiverSelectUiState(val chapter: Int = 0, val selectedCourse: Int = 1, val courses: List<Course> = emptyList(), val progress: Progress = Progress())
