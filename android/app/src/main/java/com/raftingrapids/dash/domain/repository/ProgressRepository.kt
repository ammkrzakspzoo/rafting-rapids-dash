package com.raftingrapids.dash.domain.repository

import com.raftingrapids.dash.domain.model.*
import kotlinx.coroutines.flow.StateFlow

interface ProgressRepository {
    val courses: List<Course>
    val rafts: List<RaftDesign>
    val progress: StateFlow<Progress>
    suspend fun load(): Boolean
    suspend fun update(transform: (Progress) -> Progress): Boolean
    suspend fun reset(): Boolean
}
