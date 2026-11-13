package com.raftingrapids.dash.data.repository

import com.raftingrapids.dash.domain.model.ControlSettings
import com.raftingrapids.dash.domain.repository.ProgressRepository
import com.raftingrapids.dash.domain.repository.SettingsRepository

class SettingsRepositoryImpl(private val progress: ProgressRepository) : SettingsRepository {
    override suspend fun save(settings: ControlSettings): Boolean = progress.update { it.copy(settings = settings) }
}
