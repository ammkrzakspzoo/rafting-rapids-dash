package com.raftingrapids.dash.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raftingrapids.dash.core.navigation.Screen
import com.raftingrapids.dash.domain.repository.ProgressRepository
import com.raftingrapids.dash.domain.usecase.*
import com.raftingrapids.dash.presentation.common.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class MenuViewModel(private val progress: ProgressRepository, private val daily: CreateDailyGoalsUseCase, private val complete: CompleteRunUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState = _uiState.asStateFlow()
    private val channel = Channel<UiEvent>(Channel.BUFFERED)
    val events = channel.receiveAsFlow()

    init {
        viewModelScope.launch { progress.progress.collect { data ->
            _uiState.update { it.copy(loading = data.loading, error = data.error, course = progress.courses[data.selectedCourse - 1], raft = data.selectedRaft, record = data.records[data.selectedCourse], finished = data.records.size, gold = data.goldCount, dailyComplete = data.daily[LocalDate.now().toString()]?.completeCount ?: 0) }
        } }
    }

    fun play() { if (!_uiState.value.loading) channel.trySend(UiEvent.Play(_uiState.value.course.id)) }
    fun open(screen: Screen) { channel.trySend(UiEvent.Navigate(screen)) }
    fun retry() { viewModelScope.launch { if (progress.load()) daily(LocalDate.now()); complete.retryAll() } }
}
