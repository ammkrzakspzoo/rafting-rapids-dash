package com.raftingrapids.dash.core.navigation

import com.raftingrapids.dash.domain.model.RunResult

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data object RiverSelect : Screen
    data class Game(val runId: String, val courseId: Int) : Screen
    data class GameOver(val result: RunResult) : Screen
    data object RaftGarage : Screen
    data object Challenges : Screen
    data object Settings : Screen
}
