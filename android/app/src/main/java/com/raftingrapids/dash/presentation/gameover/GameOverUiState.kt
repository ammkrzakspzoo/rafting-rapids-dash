package com.raftingrapids.dash.presentation.gameover

import com.raftingrapids.dash.domain.model.*

data class GameOverUiState(val result: RunResult? = null, val saveStatus: SaveStatus = SaveStatus.Saving, val nextAvailable: Boolean = false, val goldCount: Int = 0, val badges: Int = 0, val newBest: Boolean = false)
