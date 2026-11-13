package com.raftingrapids.dash.presentation.menu

import com.raftingrapids.dash.domain.model.*

data class MenuUiState(val loading: Boolean = true, val error: Boolean = false, val course: Course = Course(1, "springwater_start"), val raft: RaftDesign = RaftDesign.Sunrise, val record: CourseRecord? = null, val finished: Int = 0, val gold: Int = 0, val dailyComplete: Int = 0)
