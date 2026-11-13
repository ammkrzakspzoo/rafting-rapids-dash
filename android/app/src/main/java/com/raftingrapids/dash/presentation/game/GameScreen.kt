package com.raftingrapids.dash.presentation.game

import android.view.Surface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raftingrapids.dash.R
import com.raftingrapids.dash.core.input.TiltInputAdapter
import com.raftingrapids.dash.core.theme.*
import com.raftingrapids.dash.core.ui.*
import com.raftingrapids.dash.domain.model.*
import com.raftingrapids.dash.presentation.common.*
import com.raftingrapids.dash.presentation.dialog.PauseDialog
import kotlin.math.*

@Composable
fun GameRoute(viewModel: GameViewModel, runId: String, tiltInput: TiltInputAdapter) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val displayRotation = LocalView.current.display?.rotation ?: Surface.ROTATION_0
    val run = state.run
    DisposableEffect(runId, lifecycle) {
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_STOP) viewModel.pause(runId) }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer); viewModel.pause(runId) }
    }
    DisposableEffect(runId, run?.phase, run?.settings?.mode, lifecycle, displayRotation) {
        fun startInput() {
            if (run?.runId == runId && run.settings.mode == ControlMode.Tilt && (run.phase == RunPhase.Ready || run.phase == RunPhase.Running)) tiltInput.start(runId, displayRotation, viewModel::tilt)
        }
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) startInput()
            if (event == Lifecycle.Event.ON_PAUSE) tiltInput.stop(runId)
        }
        lifecycle.addObserver(observer)
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) startInput()
        onDispose { lifecycle.removeObserver(observer); tiltInput.stop(runId) }
    }
    if (run?.runId == runId) {
        GameScreen(state, viewModel::menu, viewModel::showPause, viewModel::beginDrag, viewModel::drag, viewModel::lane, viewModel::boost, viewModel::resume, viewModel::replay, viewModel::result)
        if (state.pauseVisible && run.phase == RunPhase.Paused) PauseDialog(run.elapsedMillis, run.paddles, viewModel::dismissPause, viewModel::resume, viewModel::replay, viewModel::settings, viewModel::menu)
    }
}

@Composable
fun GameScreen(state: GameUiState, onMenu: () -> Unit, onPause: () -> Unit, onDragStart: () -> Unit, onDrag: (Double) -> Unit, onLane: (Int) -> Unit, onBoost: () -> Unit, onResume: () -> Unit, onReplay: () -> Unit, onResult: () -> Unit) {
    val run = state.run ?: return
    RiverBackdrop(BackdropTone.Game) {
        BoxWithConstraints(Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 12.dp, vertical = 8.dp)) {
            val compact = maxHeight < 500.dp
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(shape = RoundedCornerShape(16.dp), color = Background.copy(alpha = 0.96f), shadowElevation = 4.dp) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = onMenu, modifier = Modifier.heightIn(min = 48.dp), contentPadding = PaddingValues(8.dp)) {
                            Image(painterResource(R.drawable.icon_home), null, Modifier.size(20.dp), contentScale = ContentScale.Fit)
                            Spacer(Modifier.width(4.dp)); Text(stringResource(R.string.menu), style = MaterialTheme.typography.labelSmall)
                        }
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.course_number, run.course.id), style = MaterialTheme.typography.labelSmall, color = PrimaryDeep)
                            Text(stringResource(run.course.nameRes()), style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                        }
                        TextButton(onClick = onPause, enabled = run.result == null, modifier = Modifier.heightIn(min = 48.dp), contentPadding = PaddingValues(8.dp)) {
                            Image(painterResource(R.drawable.icon_pause), null, Modifier.size(20.dp), contentScale = ContentScale.Fit)
                            Spacer(Modifier.width(4.dp)); Text(stringResource(R.string.pause), style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
                if (compact) {
                    Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        RiverPlayfield(run, onDragStart, onDrag, Modifier.weight(1f).fillMaxHeight())
                        Column(Modifier.width(216.dp).fillMaxHeight().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            RunHud(run)
                            GameControls(run, state.status, onLane, onBoost, onResume, onReplay, onResult, onMenu)
                        }
                    }
                } else {
                    RunHud(run)
                    RiverPlayfield(run, onDragStart, onDrag, Modifier.weight(1f).fillMaxWidth())
                    GameControls(run, state.status, onLane, onBoost, onResume, onReplay, onResult, onMenu)
                }
            }
        }
    }
}

@Composable
private fun RunHud(run: RunState) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(R.string.time_value, run.elapsedMillis / 1000.0), color = Foam, style = MaterialTheme.typography.titleMedium)
        val paddles = stringResource(R.string.paddles_hud, run.paddles)
        Text(paddles, Modifier.semantics { stateDescription = paddles; liveRegion = LiveRegionMode.Polite }, color = Foam, style = MaterialTheme.typography.labelSmall)
        val hull = stringResource(R.string.hull, run.hull)
        Text(hull, Modifier.semantics { stateDescription = hull; liveRegion = LiveRegionMode.Polite }, color = Foam, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun RiverPlayfield(run: RunState, onDragStart: () -> Unit, onDrag: (Double) -> Unit, modifier: Modifier) {
    val description = stringResource(R.string.play_area)
    val hull = stringResource(R.string.hull, run.hull)
    val shape = RoundedCornerShape(20.dp)
    BoxWithConstraints(
        modifier.clip(shape).background(
            Brush.verticalGradient(listOf(SurfaceWater.copy(alpha = 0.28f), Primary.copy(alpha = 0.3f), PrimaryDeep.copy(alpha = 0.5f)))
        )
    ) {
        val width = maxWidth
        val height = maxHeight
        val scale = width / 360f
        Box(Modifier.fillMaxSize().semantics { contentDescription = description; stateDescription = hull }
            .pointerInput(run.runId, width) {
                detectHorizontalDragGestures(onDragStart = { onDragStart() }) { change, amount ->
                    change.consume()
                    onDrag(amount.toDouble() / size.width * 360.0)
                }
            }) {
            Canvas(Modifier.fillMaxSize()) {
                val bankWidth = size.width * 0.08f
                drawRect(Rock.copy(alpha = 0.5f), size = androidx.compose.ui.geometry.Size(bankWidth, size.height))
                drawRect(Rock.copy(alpha = 0.5f), topLeft = Offset(size.width - bankWidth, 0f), size = androidx.compose.ui.geometry.Size(bankWidth, size.height))
                repeat(18) { index ->
                    val y = ((index * size.height / 9f + run.distance.toFloat() * size.width / 360f) % (size.height + 40f)) - 20f
                    val x = size.width * (0.16f + (index % 5) * 0.17f)
                    drawLine(Foam.copy(alpha = 0.35f), Offset(x, y), Offset(x - 4.dp.toPx(), y + 20.dp.toPx()), strokeWidth = 2.dp.toPx())
                }
                if (run.immunity > 0.67 && !run.settings.reducedMotion) drawRoundRect(AccentLight.copy(alpha = ((run.immunity - 0.67) / 0.18 * 0.5).toFloat().coerceIn(0f, 0.5f)), style = androidx.compose.ui.graphics.drawscope.Stroke(5.dp.toPx()), cornerRadius = androidx.compose.ui.geometry.CornerRadius(20.dp.toPx()))
            }
            run.visibleEntities.forEach { entity ->
                val ex = entity.x + if (entity.type == EntityType.Log) 10.0 * sin(run.elapsedMillis / 2400.0 * 2 * PI + entity.phase) else 0.0
                val image = when (entity.type) { EntityType.Rock -> R.drawable.sprite_5; EntityType.Log -> R.drawable.sprite_2; EntityType.Whirlpool -> R.drawable.sprite_4; EntityType.Paddle -> R.drawable.sprite_3 }
                val spriteSize = when (entity.type) { EntityType.Rock -> 48.dp; EntityType.Log -> 64.dp; EntityType.Whirlpool -> 80.dp; EntityType.Paddle -> 32.dp }
                val y = height * 0.78f - scale * (entity.distance - run.distance).toFloat()
                Image(painterResource(image), null, Modifier.offset(width * (ex / 360.0).toFloat() - spriteSize / 2, y - spriteSize / 2).size(spriteSize)
                    .graphicsLayer {
                        if (entity.type == EntityType.Whirlpool && !run.settings.reducedMotion) rotationZ = (run.elapsedMillis % 3000) / 3000f * 360f
                        run.pickupTicks[entity.id]?.let { tick ->
                            val progress = ((run.ticks - tick) / 11f).coerceIn(0f, 1f)
                            alpha = 1f - progress
                            if (!run.settings.reducedMotion) { scaleX = 1f - progress * 0.65f; scaleY = scaleX }
                        }
                    }, contentScale = ContentScale.Fit)
            }
            if (run.course.length - run.distance < 1000) {
                val y = height * 0.78f - scale * (run.course.length - run.distance).toFloat()
                Canvas(Modifier.offset(y = y).fillMaxWidth().height(16.dp)) {
                    repeat(16) { i -> drawRect(if (i % 2 == 0) Foam else PrimaryDeep, Offset(i * size.width / 16f, 0f), androidx.compose.ui.geometry.Size(size.width / 16f, size.height)) }
                }
            }
            RaftSprite(run.raft, 52.dp, Modifier.offset(width * (run.x / 360.0).toFloat() - 26.dp, height * 0.78f - 26.dp)
                .graphicsLayer {
                    rotationZ = if (run.spinRemaining > 0) ((1.2 - run.spinRemaining) / 1.2 * 360).toFloat() else ((run.targetX - run.x) / 10).toFloat().coerceIn(-10f, 10f)
                    if (!run.settings.reducedMotion) {
                        val sincePickup = run.pickupTicks.values.maxOrNull()?.let { run.ticks - it } ?: 100
                        if (sincePickup < 11) { scaleX = 1f + 0.04f * sin(sincePickup / 11f * PI).toFloat(); scaleY = scaleX }
                    }
                })
            if (run.phase == RunPhase.Ready) Text(stringResource(R.string.get_ready), Modifier.align(Alignment.Center), style = MaterialTheme.typography.headlineMedium, color = Foam)
            LinearProgressIndicator(progress = { (run.distance / run.course.length).toFloat().coerceIn(0f, 1f) }, modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().height(4.dp), color = Accent, trackColor = Foam.copy(alpha = 0.25f))
        }
    }
}

@Composable
private fun GameControls(run: RunState, status: RiverStatus, onLane: (Int) -> Unit, onBoost: () -> Unit, onResume: () -> Unit, onReplay: () -> Unit, onResult: () -> Unit, onMenu: () -> Unit) {
    val message = stringResource(when (status) {
        RiverStatus.Flow -> R.string.steering_hint; RiverStatus.Hit -> R.string.hit_status; RiverStatus.Paddle -> R.string.pickup_status
        RiverStatus.Spin -> R.string.spin_status; RiverStatus.BoostReady -> R.string.boost_ready; RiverStatus.Boosting -> R.string.boost_active; RiverStatus.Calibrated -> R.string.tilt_calibrated
    })
    RiverCard(Modifier.fillMaxWidth()) {
        if (run.result != null) {
            Text(stringResource(if (run.phase == RunPhase.Finished) R.string.run_finished else R.string.run_ended), style = MaterialTheme.typography.titleMedium)
            PrimaryButton(stringResource(R.string.view_result), onResult)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SecondaryButton(stringResource(R.string.play_again), onReplay, Modifier.weight(1f))
                SecondaryButton(stringResource(R.string.menu), onMenu, Modifier.weight(1f))
            }
        } else if (run.phase == RunPhase.Paused) {
            Text(stringResource(R.string.paused_hint), style = MaterialTheme.typography.bodyMedium)
            PrimaryButton(stringResource(R.string.resume), onResume)
        } else {
            Text(message, Modifier.semantics { stateDescription = message; liveRegion = LiveRegionMode.Polite }, style = MaterialTheme.typography.labelSmall)
            BoostMeter(run.charge, run.boostRemaining > 0)
            PrimaryButton(stringResource(if (run.boostRemaining > 0) R.string.boosting else R.string.boost), onBoost, enabled = run.phase == RunPhase.Running && run.charge >= 99.99 && run.boostRemaining <= 0)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SecondaryButton(stringResource(R.string.left), { onLane(-1) }, Modifier.weight(1f))
                SecondaryButton(stringResource(R.string.right), { onLane(1) }, Modifier.weight(1f))
            }
        }
    }
}
