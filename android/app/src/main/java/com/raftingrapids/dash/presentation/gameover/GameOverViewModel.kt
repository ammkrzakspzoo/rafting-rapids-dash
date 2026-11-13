package com.raftingrapids.dash.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raftingrapids.dash.domain.model.*
import com.raftingrapids.dash.domain.repository.ProgressRepository
import com.raftingrapids.dash.domain.usecase.CompleteRunUseCase
import com.raftingrapids.dash.presentation.common.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameOverViewModel(private val progress: ProgressRepository, private val complete: CompleteRunUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(GameOverUiState())
    val uiState = _uiState.asStateFlow()
    private val channel = Channel<UiEvent>(Channel.BUFFERED)
    val events = channel.receiveAsFlow()
    init {
        viewModelScope.launch {
            combine(progress.progress, complete.status) { data, saves -> data to saves }.collect { (data, saves) ->
                _uiState.update { state -> state.copy(saveStatus = state.result?.runId?.let { saves[it] } ?: SaveStatus.Saving,
                    nextAvailable = state.result?.let { it.completed && it.course.id < 40 && data.unlockedThrough > it.course.id } ?: false, goldCount = data.goldCount, badges = data.dailyBadges) }
            }
        }
    }
    fun bind(result: RunResult) {
        if (_uiState.value.result?.runId == result.runId) return
        _uiState.update { it.copy(result = result, saveStatus = complete.status.value[result.runId] ?: SaveStatus.Saving,
            newBest = result.completed && (result.previousBestMillis == null || result.elapsedMillis < result.previousBestMillis),
            nextAvailable = result.completed && result.course.id < 40 && progress.progress.value.unlockedThrough > result.course.id) }
    }
    fun retry() { _uiState.value.result?.let { result -> viewModelScope.launch { complete.retry(result.runId) } } }
    fun replay() { _uiState.value.result?.let { channel.trySend(UiEvent.Play(it.course.id, true)) } }
    fun next() { if (_uiState.value.nextAvailable) _uiState.value.result?.let { channel.trySend(UiEvent.Play(it.course.id + 1, true)) } }
    fun menu() { channel.trySend(UiEvent.Menu) }
}
