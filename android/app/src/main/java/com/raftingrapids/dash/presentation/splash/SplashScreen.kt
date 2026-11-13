package com.raftingrapids.dash.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.raftingrapids.dash.R
import com.raftingrapids.dash.core.theme.*
import com.raftingrapids.dash.core.ui.*

@Composable
fun SplashRoute(viewModel: SplashViewModel, onFinished: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val finished by viewModel.finished.collectAsStateWithLifecycle()
    LaunchedEffect(finished) { if (finished && viewModel.consumeFinished()) onFinished() }
    SplashScreen(state)
}

@Composable
fun SplashScreen(state: SplashUiState) {
    val transition = rememberInfiniteTransition(label = "river")
    val bob by transition.animateFloat(-6f, 6f, infiniteRepeatable(tween(1600), RepeatMode.Reverse), label = "bob")
    val rock by transition.animateFloat(-4f, 4f, infiniteRepeatable(tween(1800), RepeatMode.Reverse), label = "rock")
    val opening = stringResource(R.string.opening)
    RiverBackdrop(BackdropTone.Splash) {
        Column(Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()).padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(stringResource(R.string.expedition), style = MaterialTheme.typography.labelSmall, color = SurfaceWater)
            Spacer(Modifier.height(40.dp))
            Surface(Modifier.size(208.dp), shape = CircleShape, color = Primary.copy(alpha = 0.18f), border = BorderStroke(1.dp, Foam.copy(alpha = 0.25f))) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(Modifier.size(176.dp), shape = CircleShape, color = Foam.copy(alpha = 0.06f), border = BorderStroke(1.dp, Foam.copy(alpha = 0.15f))) {}
                    Image(painterResource(R.drawable.logo), null, Modifier.size(152.dp).graphicsLayer { rotationZ = if (state.reducedMotion) 0f else rock; translationY = if (state.reducedMotion) 0f else bob }, contentScale = ContentScale.Fit)
                }
            }
            Spacer(Modifier.height(32.dp))
            Text(stringResource(R.string.title_first), style = MaterialTheme.typography.displaySmall, color = Foam, modifier = Modifier.semantics { heading() })
            Text(stringResource(R.string.title_second), style = MaterialTheme.typography.displaySmall, color = Foam)
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.tagline), style = MaterialTheme.typography.bodyLarge, color = Background, textAlign = TextAlign.Center)
            Spacer(Modifier.height(32.dp))
            LinearProgressIndicator(modifier = Modifier.width(168.dp).height(4.dp).semantics { contentDescription = opening }, color = Accent, trackColor = SurfaceWater.copy(alpha = 0.28f))
        }
    }
}
