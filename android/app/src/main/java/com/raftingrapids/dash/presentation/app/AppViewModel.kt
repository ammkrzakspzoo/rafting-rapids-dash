package com.raftingrapids.dash.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raftingrapids.dash.core.navigation.Screen
import com.raftingrapids.dash.domain.repository.ProgressRepository
import com.raftingrapids.dash.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

class AppViewModel(private val progress: ProgressRepository, private val daily: CreateDailyGoalsUseCase, private val complete: CompleteRunUseCase) : ViewModel() {
    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen = _screen.asStateFlow()
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState = _uiState.asStateFlow()
    private val backStack = mutableListOf<Screen>()

    init {
        viewModelScope.launch {
            if (progress.load()) daily(LocalDate.now())
        }
        viewModelScope.launch { progress.progress.collect { data -> _uiState.update { it.copy(reducedMotion = data.settings.reducedMotion) } } }
    }

    fun finishSplash() {
        if (_screen.value == Screen.Splash) publish(Screen.Menu)
    }

    fun navigate(next: Screen) {
        if (next == _screen.value) return
        if (_screen.value != Screen.Splash) backStack += _screen.value
        publish(next)
    }

    fun startGame(courseId: Int, replay: Boolean): Screen.Game? {
        if (!replay && (_screen.value is Screen.Game || _screen.value is Screen.GameOver)) return null
        if (progress.progress.value.loading || courseId !in 1..progress.progress.value.unlockedThrough) return null
        val next = Screen.Game(UUID.randomUUID().toString(), courseId)
        if (replay) {
            while (backStack.lastOrNull() is Screen.Game || backStack.lastOrNull() is Screen.GameOver) backStack.removeAt(backStack.lastIndex)
            publish(next)
        } else navigate(next)
        return next
    }

    fun back() {
        if (backStack.isNotEmpty()) publish(backStack.removeAt(backStack.lastIndex))
    }

    fun menu() {
        backStack.clear()
        publish(Screen.Menu)
    }

    fun refreshDate() {
        viewModelScope.launch {
            if (!progress.progress.value.loading && !progress.progress.value.unreadable) daily(LocalDate.now())
            complete.retryAll()
        }
    }

    private fun publish(next: Screen) {
        _screen.update { next }
        _uiState.update { it.copy(screen = next, backStack = backStack.toList()) }
    }
}
