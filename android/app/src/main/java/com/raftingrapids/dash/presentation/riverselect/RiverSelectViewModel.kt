package com.raftingrapids.dash.presentation.riverselect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raftingrapids.dash.domain.repository.ProgressRepository
import com.raftingrapids.dash.presentation.common.UiEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RiverSelectViewModel(private val progress: ProgressRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RiverSelectUiState(courses = progress.courses))
    val uiState = _uiState.asStateFlow()
    private val channel = Channel<UiEvent>(Channel.BUFFERED)
    val events = channel.receiveAsFlow()
    private var initialized = false
    init {
        viewModelScope.launch { progress.progress.collect { data ->
            _uiState.update { it.copy(progress = data, selectedCourse = if (!initialized || it.selectedCourse > data.unlockedThrough && it.progress.unlockedThrough > data.unlockedThrough) data.selectedCourse else it.selectedCourse, chapter = if (!initialized) (data.selectedCourse - 1) / 8 else it.chapter) }
            if (!data.loading) initialized = true
        } }
    }
    fun chapter(value: Int) { _uiState.update { it.copy(chapter = value) } }
    fun select(id: Int) {
        _uiState.update { it.copy(selectedCourse = id) }
        if (id <= progress.progress.value.unlockedThrough) viewModelScope.launch { progress.update { it.copy(selectedCourse = id) } }
    }
    fun play() { if (_uiState.value.selectedCourse <= _uiState.value.progress.unlockedThrough) channel.trySend(UiEvent.Play(_uiState.value.selectedCourse)) }
    fun retry() { viewModelScope.launch { progress.load() } }
}
