package com.raftingrapids.dash.presentation.challenges

import com.raftingrapids.dash.domain.model.*

data class ChallengesUiState(val date: String = "", val goal: DailyGoal? = null, val course: Course = Course(1, "springwater_start"), val progress: Progress = Progress())
