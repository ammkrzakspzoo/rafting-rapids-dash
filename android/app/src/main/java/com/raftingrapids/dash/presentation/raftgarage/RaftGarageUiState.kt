package com.raftingrapids.dash.presentation.raftgarage

import com.raftingrapids.dash.domain.model.*

data class RaftGarageUiState(val focused: RaftDesign = RaftDesign.Sunrise, val rafts: List<RaftDesign> = emptyList(), val progress: Progress = Progress())
