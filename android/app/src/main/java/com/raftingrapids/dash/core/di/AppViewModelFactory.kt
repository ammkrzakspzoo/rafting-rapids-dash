package com.raftingrapids.dash.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raftingrapids.dash.core.feedback.PlatformFeedback
import com.raftingrapids.dash.domain.game.*
import com.raftingrapids.dash.domain.repository.*
import com.raftingrapids.dash.domain.usecase.*
import com.raftingrapids.dash.presentation.app.AppViewModel
import com.raftingrapids.dash.presentation.splash.SplashViewModel
import com.raftingrapids.dash.presentation.menu.MenuViewModel
import com.raftingrapids.dash.presentation.game.GameViewModel
import com.raftingrapids.dash.presentation.gameover.GameOverViewModel
import com.raftingrapids.dash.presentation.riverselect.RiverSelectViewModel
import com.raftingrapids.dash.presentation.raftgarage.RaftGarageViewModel
import com.raftingrapids.dash.presentation.challenges.ChallengesViewModel
import com.raftingrapids.dash.presentation.settings.SettingsViewModel

class AppViewModelFactory(
    private val progress: ProgressRepository,
    private val settings: SettingsRepository,
    private val daily: CreateDailyGoalsUseCase,
    private val complete: CompleteRunUseCase,
    private val generator: RiverGenerator,
    private val engine: RapidsEngine,
    private val feedback: PlatformFeedback,
    private val tiltAvailable: Boolean,
    private val accessibleControls: Boolean
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        AppViewModel::class.java -> AppViewModel(progress, daily, complete)
        SplashViewModel::class.java -> SplashViewModel(progress)
        MenuViewModel::class.java -> MenuViewModel(progress, daily, complete)
        GameViewModel::class.java -> GameViewModel(progress, generator, engine, complete, daily, feedback, tiltAvailable, accessibleControls)
        GameOverViewModel::class.java -> GameOverViewModel(progress, complete)
        RiverSelectViewModel::class.java -> RiverSelectViewModel(progress)
        RaftGarageViewModel::class.java -> RaftGarageViewModel(progress)
        ChallengesViewModel::class.java -> ChallengesViewModel(progress, daily)
        SettingsViewModel::class.java -> SettingsViewModel(progress, settings, complete, daily, tiltAvailable && !accessibleControls)
        else -> error(modelClass.name)
    } as T
}
