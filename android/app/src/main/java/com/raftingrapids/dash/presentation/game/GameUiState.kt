package com.raftingrapids.dash.presentation.game

import com.raftingrapids.dash.domain.model.RunState

enum class RiverStatus { Flow, Hit, Paddle, Spin, BoostReady, Boosting, Calibrated }

data class GameUiState(val run: RunState? = null, val pauseVisible: Boolean = false, val status: RiverStatus = RiverStatus.Flow)
