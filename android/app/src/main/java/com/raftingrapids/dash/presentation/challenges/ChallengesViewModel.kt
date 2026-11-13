package com.raftingrapids.dash.presentation.challenges

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raftingrapids.dash.domain.repository.ProgressRepository
import com.raftingrapids.dash.domain.usecase.CreateDailyGoalsUseCase
import com.raftingrapids.dash.presentation.common.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class ChallengesViewModel(private val progress: ProgressRepository, private val daily: CreateDailyGoalsUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(ChallengesUiState())
    val uiState = _uiState.asStateFlow()
    private val channel = Channel<UiEvent>(Channel.BUFFERED)
    val events = channel.receiveAsFlow()
    init { viewModelScope.launch { progress.progress.collect { data ->
        val date = LocalDate.now().toString()
        val goal = data.daily[date]
        _uiState.update { it.copy(date = date, goal = goal, course = progress.courses[(goal?.courseId ?: 1) - 1], progress = data) }
    } } }
    fun play() { channel.trySend(UiEvent.Play(_uiState.value.course.id)) }
    fun retry() { viewModelScope.launch { if (progress.load()) daily(LocalDate.now()) } }
}
