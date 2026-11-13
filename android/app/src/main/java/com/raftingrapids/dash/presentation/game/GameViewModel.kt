package com.raftingrapids.dash.presentation.game

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raftingrapids.dash.core.config.GameConfig
import com.raftingrapids.dash.core.feedback.PlatformFeedback
import com.raftingrapids.dash.core.navigation.Screen
import com.raftingrapids.dash.domain.game.*
import com.raftingrapids.dash.domain.model.*
import com.raftingrapids.dash.domain.repository.ProgressRepository
import com.raftingrapids.dash.domain.usecase.CompleteRunUseCase
import com.raftingrapids.dash.domain.usecase.CreateDailyGoalsUseCase
import com.raftingrapids.dash.presentation.common.UiEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.time.LocalDate

class GameViewModel(
    private val progress: ProgressRepository,
    private val generator: RiverGenerator,
    private val engine: RapidsEngine,
    private val complete: CompleteRunUseCase,
    private val daily: CreateDailyGoalsUseCase,
    private val feedback: PlatformFeedback,
    private val tiltAvailable: Boolean,
    private val accessibleControls: Boolean
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState = _uiState.asStateFlow()
    private val channel = Channel<UiEvent>(Channel.BUFFERED)
    val events = channel.receiveAsFlow()
    private var loop: Job? = null
    private var tilt = 0.0
    private var dragOrigin = 180.0
    private var dragDelta = 0.0

    fun startRound(runId: String, courseId: Int) {
        if (_uiState.value.run?.runId == runId) return
        val data = progress.progress.value
        if (courseId !in 1..data.unlockedThrough) return
        loop?.cancel()
        val course = progress.courses[courseId - 1]
        val settings = effective(data.settings)
        val run = RunState(runId, course, LocalDate.now().toString(), data.selectedRaft, settings, generator.generate(course), previousBestMillis = data.records[courseId]?.bestMillis)
        _uiState.update { it.copy(run = run, pauseVisible = false, status = RiverStatus.Flow) }
        tilt = 0.0
        begin(runId, ready = true)
    }

    private fun effective(settings: ControlSettings): ControlSettings = if (!tiltAvailable || accessibleControls) settings.copy(mode = ControlMode.Drag) else settings

    private fun begin(runId: String, ready: Boolean) {
        loop?.cancel()
        loop = viewModelScope.launch {
            if (ready) _uiState.value.run?.date?.let { daily(LocalDate.parse(it)) }
            if (ready) delay(GameConfig.READY_DURATION_MS)
            if (_uiState.value.run?.runId != runId) return@launch
            _uiState.update { it.copy(run = it.run?.copy(phase = RunPhase.Running)) }
            var previous = SystemClock.elapsedRealtimeNanos()
            var accumulator = 0.0
            while (isActive && _uiState.value.run?.runId == runId) {
                delay(16)
                val now = SystemClock.elapsedRealtimeNanos()
                accumulator = (accumulator + (now - previous) / 1_000_000_000.0).coerceAtMost(0.1)
                previous = now
                var count = 0
                while (accumulator + 0.0000001 >= GameConfig.STEP_SECONDS && count < 6) {
                    val before = _uiState.value.run ?: return@launch
                    if (before.phase != RunPhase.Running) return@launch
                    val input = if (before.settings.mode == ControlMode.Tilt) engine.steer(before, before.targetX + tilt * 240.0 * before.settings.multiplier * GameConfig.STEP_SECONDS) else before
                    val next = engine.step(input)
                    val status = when {
                        next.hull < before.hull -> RiverStatus.Hit
                        next.charge >= 99.99 && before.charge < 99.99 -> RiverStatus.BoostReady
                        next.paddles > before.paddles -> RiverStatus.Paddle
                        next.spinRemaining > before.spinRemaining -> RiverStatus.Spin
                        next.boostRemaining <= 0.0 && before.boostRemaining > 0.0 -> RiverStatus.Flow
                        else -> _uiState.value.status
                    }
                    _uiState.update { it.copy(run = next, status = status) }
                    if (next.hull < before.hull) feedback.play(3, next.settings)
                    else if (next.charge >= 99.99 && before.charge < 99.99) feedback.play(1, next.settings)
                    else if (next.paddles > before.paddles) feedback.play(0, next.settings)
                    accumulator -= GameConfig.STEP_SECONDS
                    count++
                    val result = next.result
                    if (result != null) {
                        feedback.play(2, next.settings)
                        viewModelScope.launch { complete(result) }
                        channel.send(UiEvent.Navigate(Screen.GameOver(result)))
                        return@launch
                    }
                }
            }
        }
    }

    fun pause(runId: String) {
        val run = _uiState.value.run ?: return
        if (run.runId != runId) return
        loop?.cancel()
        feedback.release()
        if (run.phase == RunPhase.Ready || run.phase == RunPhase.Running) _uiState.update { it.copy(run = run.copy(phase = RunPhase.Paused), pauseVisible = true) }
    }

    fun resume() {
        val run = _uiState.value.run ?: return
        if (run.phase != RunPhase.Paused) return
        val settings = effective(progress.progress.value.settings)
        tilt = 0.0
        _uiState.update { it.copy(run = run.copy(settings = settings), pauseVisible = false, status = if (settings.mode == ControlMode.Tilt) RiverStatus.Calibrated else RiverStatus.Flow) }
        begin(run.runId, ready = false)
    }

    fun dismissPause() { _uiState.update { it.copy(pauseVisible = false) } }
    fun showPause() { _uiState.value.run?.let { pause(it.runId) }; _uiState.update { it.copy(pauseVisible = true) } }
    fun tilt(value: Double) { tilt = value }
    fun beginDrag() { dragOrigin = _uiState.value.run?.x ?: 180.0; dragDelta = 0.0 }
    fun drag(delta: Double) {
        val run = _uiState.value.run ?: return
        if (run.phase != RunPhase.Ready && run.phase != RunPhase.Running) return
        dragDelta += delta * run.settings.multiplier
        _uiState.update { it.copy(run = engine.steer(run, dragOrigin + dragDelta)) }
    }
    fun lane(direction: Int) {
        val run = _uiState.value.run ?: return
        if (run.phase != RunPhase.Ready && run.phase != RunPhase.Running) return
        _uiState.update { it.copy(run = engine.lane(run, direction)) }
    }
    fun boost() { _uiState.update { state -> state.run?.let { state.copy(run = engine.boost(it), status = if (it.charge >= 99.99) RiverStatus.Boosting else state.status) } ?: state } }
    fun replay() { _uiState.value.run?.let { channel.trySend(UiEvent.Play(it.course.id, true)) } }
    fun result() { _uiState.value.run?.result?.let { channel.trySend(UiEvent.Navigate(Screen.GameOver(it))) } }
    fun menu() { _uiState.value.run?.let { pause(it.runId) }; channel.trySend(UiEvent.Menu) }
    fun settings() { _uiState.update { it.copy(pauseVisible = false) }; channel.trySend(UiEvent.Navigate(Screen.Settings)) }
    override fun onCleared() { loop?.cancel(); feedback.release() }
}
