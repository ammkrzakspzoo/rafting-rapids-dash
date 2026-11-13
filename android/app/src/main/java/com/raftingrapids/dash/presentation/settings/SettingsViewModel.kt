package com.raftingrapids.dash.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raftingrapids.dash.domain.model.ControlSettings
import com.raftingrapids.dash.domain.repository.*
import com.raftingrapids.dash.domain.usecase.*
import com.raftingrapids.dash.presentation.common.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class SettingsViewModel(private val progress: ProgressRepository, private val settings: SettingsRepository, private val complete: CompleteRunUseCase, private val daily: CreateDailyGoalsUseCase, tiltAvailable: Boolean) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState(tiltAvailable = tiltAvailable))
    val uiState = _uiState.asStateFlow()
    private val channel = Channel<UiEvent>(Channel.BUFFERED)
    val events = channel.receiveAsFlow()
    private var pending: ControlSettings? = null

    init { viewModelScope.launch { progress.progress.collect { data -> _uiState.update { it.copy(settings = if (it.saving) it.settings else data.settings, error = data.error, finished = data.records.size, gold = data.goldCount) } } } }
    fun change(transform: (ControlSettings) -> ControlSettings) {
        if (_uiState.value.saving) return
        save(transform(_uiState.value.settings))
    }
    private fun save(value: ControlSettings) {
        pending = value
        _uiState.update { it.copy(settings = value, saving = true) }
        viewModelScope.launch {
            val success = settings.save(value)
            if (success) pending = null
            _uiState.update { it.copy(saving = false, error = !success, settings = progress.progress.value.settings) }
        }
    }
    fun retry() { pending?.let { save(it) } ?: run { viewModelScope.launch { if (progress.load()) daily(LocalDate.now()); complete.retryAll() } } }
    fun requestReset() { _uiState.update { it.copy(resetVisible = true) } }
    fun dismissReset() { _uiState.update { it.copy(resetVisible = false) } }
    fun reset() {
        if (_uiState.value.saving) return
        _uiState.update { it.copy(saving = true) }
        viewModelScope.launch {
            val success = complete.resetProgress()
            if (success) {
                pending = null
                daily(LocalDate.now())
                channel.send(UiEvent.Menu)
            }
            _uiState.update { it.copy(saving = false, resetVisible = !success, error = !success) }
        }
    }
}
