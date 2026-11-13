package com.raftingrapids.dash.presentation.raftgarage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raftingrapids.dash.domain.model.*
import com.raftingrapids.dash.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RaftGarageViewModel(private val progress: ProgressRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RaftGarageUiState(rafts = progress.rafts))
    val uiState = _uiState.asStateFlow()
    private var pending: RaftDesign? = null
    init { viewModelScope.launch { progress.progress.collect { data -> _uiState.update { it.copy(progress = data) } } } }
    fun focus(raft: RaftDesign) { _uiState.update { it.copy(focused = raft) } }
    fun select() {
        val raft = _uiState.value.focused
        if (!raft.isUnlocked(progress.progress.value)) return
        pending = raft
        viewModelScope.launch { if (progress.update { it.copy(selectedRaft = raft) }) pending = null }
    }
    fun retry() { if (pending != null) select() else viewModelScope.launch { progress.load() } }
}
