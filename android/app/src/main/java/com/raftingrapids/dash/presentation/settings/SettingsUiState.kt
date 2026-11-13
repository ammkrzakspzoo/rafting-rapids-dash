package com.raftingrapids.dash.presentation.settings

import com.raftingrapids.dash.domain.model.ControlSettings

data class SettingsUiState(val settings: ControlSettings = ControlSettings(), val tiltAvailable: Boolean = false, val saving: Boolean = false, val error: Boolean = false, val resetVisible: Boolean = false, val finished: Int = 0, val gold: Int = 0)
