package com.raftingrapids.dash.presentation.app

import com.raftingrapids.dash.core.navigation.Screen

data class AppUiState(val screen: Screen = Screen.Splash, val backStack: List<Screen> = emptyList(), val reducedMotion: Boolean = false)
