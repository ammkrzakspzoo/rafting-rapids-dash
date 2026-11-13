package com.raftingrapids.dash.domain.repository

import com.raftingrapids.dash.domain.model.ControlSettings

interface SettingsRepository {
    suspend fun save(settings: ControlSettings): Boolean
}
