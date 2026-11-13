package com.raftingrapids.dash.presentation.app

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.raftingrapids.dash.core.di.ServiceLocator
import com.raftingrapids.dash.core.navigation.Screen
import com.raftingrapids.dash.presentation.common.UiEvent
import com.raftingrapids.dash.presentation.splash.*
import com.raftingrapids.dash.presentation.menu.*
import com.raftingrapids.dash.presentation.game.*
import com.raftingrapids.dash.presentation.gameover.*
import com.raftingrapids.dash.presentation.riverselect.*
import com.raftingrapids.dash.presentation.raftgarage.*
import com.raftingrapids.dash.presentation.challenges.*
import com.raftingrapids.dash.presentation.settings.*
import kotlinx.coroutines.flow.merge

@Composable
fun AppRoot() {
    val factory = ServiceLocator.factory
    val app: AppViewModel = viewModel(factory = factory)
    val splash: SplashViewModel = viewModel(factory = factory)
    val menu: MenuViewModel = viewModel(factory = factory)
    val game: GameViewModel = viewModel(factory = factory)
    val result: GameOverViewModel = viewModel(factory = factory)
    val rivers: RiverSelectViewModel = viewModel(factory = factory)
    val garage: RaftGarageViewModel = viewModel(factory = factory)
    val challenges: ChallengesViewModel = viewModel(factory = factory)
    val settings: SettingsViewModel = viewModel(factory = factory)
    val screen by app.screen.collectAsStateWithLifecycle()
    val state by app.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val view = LocalView.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    SideEffect {
        (context as? Activity)?.window?.let { window ->
            val light = screen != Screen.Splash && screen !is Screen.Game
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = light
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = light
        }
    }
    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_RESUME) app.refreshDate() }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    fun handleEvent(event: UiEvent) {
        when (event) {
            is UiEvent.Play -> app.startGame(event.courseId, event.replay)?.let { game.startRound(it.runId, it.courseId) }
            is UiEvent.Navigate -> {
                val route = event.screen
                if (route is Screen.GameOver) result.bind(route.result)
                app.navigate(route)
            }
            UiEvent.Menu -> app.menu()
        }
    }
    LaunchedEffect(Unit) {
        merge(menu.events, game.events, result.events, rivers.events, challenges.events, settings.events).collect(::handleEvent)
    }
    val back: () -> Unit = {
        val current = app.screen.value
        if (current is Screen.Game) game.pause(current.runId)
        if (current == Screen.Menu || current == Screen.Splash) (context as? Activity)?.finish() else app.back()
    }
    BackHandler(onBack = back)
    AnimatedContent(targetState = screen, transitionSpec = {
        fadeIn(tween(if (state.reducedMotion) 150 else 300)) togetherWith fadeOut(tween(if (state.reducedMotion) 150 else 220))
    }, label = "river_routes") { route ->
        when (route) {
            Screen.Splash -> SplashRoute(splash, app::finishSplash)
            Screen.Menu -> MenuRoute(menu)
            Screen.RiverSelect -> RiverSelectRoute(rivers, back)
            is Screen.Game -> GameRoute(game, route.runId, ServiceLocator.tiltInput)
            is Screen.GameOver -> GameOverRoute(result)
            Screen.RaftGarage -> RaftGarageRoute(garage, back)
            Screen.Challenges -> ChallengesRoute(challenges, back)
            Screen.Settings -> SettingsRoute(settings, back)
        }
    }
}
