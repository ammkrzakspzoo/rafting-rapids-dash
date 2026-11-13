package com.raftingrapids.dash.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raftingrapids.dash.core.config.GameConfig
import com.raftingrapids.dash.domain.repository.ProgressRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SplashViewModel(progress: ProgressRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()
    private val _finished = MutableStateFlow(false)
    val finished = _finished.asStateFlow()
    private var delivered = false

    init {
        viewModelScope.launch { progress.progress.collect { data -> _uiState.update { it.copy(reducedMotion = data.settings.reducedMotion) } } }
        viewModelScope.launch {
            delay(GameConfig.LOADER_DURATION_MS)
            _finished.value = true
        }
    }

    fun consumeFinished(): Boolean {
        if (!_finished.value || delivered) return false
        delivered = true
        return true
    }
}
