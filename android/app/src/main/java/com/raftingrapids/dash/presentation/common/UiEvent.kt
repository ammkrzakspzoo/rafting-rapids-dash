package com.raftingrapids.dash.presentation.common

import com.raftingrapids.dash.core.navigation.Screen

sealed interface UiEvent {
    data class Navigate(val screen: Screen) : UiEvent
    data class Play(val courseId: Int, val replay: Boolean = false) : UiEvent
    data object Menu : UiEvent
}
