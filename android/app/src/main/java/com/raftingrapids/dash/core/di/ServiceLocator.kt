package com.raftingrapids.dash.core.di

import android.content.Context
import android.view.accessibility.AccessibilityManager
import com.raftingrapids.dash.core.feedback.PlatformFeedback
import com.raftingrapids.dash.core.input.TiltInputAdapter
import com.raftingrapids.dash.data.local.PreferencesStore
import com.raftingrapids.dash.data.repository.*
import com.raftingrapids.dash.domain.game.*
import com.raftingrapids.dash.domain.repository.*
import com.raftingrapids.dash.domain.usecase.*

object ServiceLocator {
    private data class Services(val factory: AppViewModelFactory, val tilt: TiltInputAdapter)
    private var services: Services? = null
    val factory: AppViewModelFactory get() = requireNotNull(services).factory
    val tiltInput: TiltInputAdapter get() = requireNotNull(services).tilt

    fun initialize(context: Context) {
        if (services != null) return
        val progress: ProgressRepository = ProgressRepositoryImpl(PreferencesStore(context.applicationContext))
        val settings: SettingsRepository = SettingsRepositoryImpl(progress)
        val daily = CreateDailyGoalsUseCase(progress)
        val complete = CompleteRunUseCase(progress, daily)
        val tilt = TiltInputAdapter(context.applicationContext)
        val accessibility = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        services = Services(AppViewModelFactory(progress, settings, daily, complete, RiverGenerator(), RapidsEngine(), PlatformFeedback(context.applicationContext), tilt.available, accessibility.isTouchExplorationEnabled), tilt)
    }
}
