package com.stephenwanjala.multiply.game.feat_bubblemode

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stephenwanjala.multiply.R
import com.stephenwanjala.multiply.game.components.FloatingSymbols
import com.stephenwanjala.multiply.game.components.confettiEffect
import com.stephenwanjala.multiply.ui.theme.LocalMultiplyColors
import com.stephenwanjala.multiply.ui.theme.MultiplyTheme
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
    toSettings: () -> Unit,
    toHowToPlay: () -> Unit,
    ontoHome: () -> Unit
) {
    val screenSize = currentWindowSize()
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(screenSize) {
        viewModel.onEvent(BubbleGameEvent.UpdateScreenHeight(screenSize.height.toFloat()))
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                BubbleGameEffect.PlayCorrectAnswerHaptic ->
                    haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                BubbleGameEffect.PlayWrongAnswerHaptic ->
                    haptic.performHapticFeedback(HapticFeedbackType.Reject)
                BubbleGameEffect.PlayGameOverHaptic ->
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            }
        }
    }

    GameScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
        onNavigateUp = onNavigateUp,
        toSettings = toSettings,
        toHowToPlay = toHowToPlay,
        ontoHome = ontoHome
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameScreenContent(
    state: GameState,
    onEvent: (BubbleGameEvent) -> Unit,
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
    toSettings: () -> Unit,
    toHowToPlay: () -> Unit,
    ontoHome: () -> Unit
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = state.selectedDifficulty.name.lowercase()
                        .replaceFirstChar { it.titlecase() } + " mode",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        },
            navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Navigate Up"
                    )
                }
            }, actions = {
                if (state.gameActive) {
                    IconButton(onClick = {
                        if (state.isPaused) onEvent(BubbleGameEvent.ResumeGame)
                        else onEvent(BubbleGameEvent.PauseGame)
                    }) {
                        PulsatingPauseIcon(isPaused = state.isPaused)
                    }
                }
                IconButton(onClick = toSettings) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                }
                IconButton(onClick = toHowToPlay) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "How To Play")
                }
            })
    }) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(paddingValues)
                .padding(paddingValues = paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            LocalMultiplyColors.current.bubbleBackground.copy(alpha = 0.35f),
                            MaterialTheme.colorScheme.background,
                            LocalMultiplyColors.current.bubbleBackground.copy(alpha = 0.25f)
                        )
                    )
                )
        ) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                if (!state.gameActive && !state.showGameOverDialog) {
                    ReadyPrompt(
                        difficulty = state.selectedDifficulty.name,
                        highScore = state.highScore,
                        onStart = { onEvent(BubbleGameEvent.StartGame) }
                    )
                } else if (state.gameActive) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        HudBar(state = state)
                        Spacer(Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(bottom = 8.dp)
                                .onSizeChanged { layoutSize ->
                                    onEvent(BubbleGameEvent.UpdateGameAreaHeight(layoutSize.height.toFloat()))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            FloatingSymbols()
                            if ((state.currentProblem?.position ?: 0f) <= state.safeAreaHeight) {
                                MathBubble(
                                    state = state,
                                    modifier = Modifier.align(Alignment.TopCenter)
                                )
                            }
                            PauseOverlay(
                                visible = state.isPaused,
                                onResume = { onEvent(BubbleGameEvent.ResumeGame) },
                                onRestart = { onEvent(BubbleGameEvent.RestartGame) },
                                onQuit = ontoHome
                            )
                        }

                        AnswerButtons(state = state, submitAnswer = {
                            onEvent(BubbleGameEvent.SubmitAnswer(it))
                        })
                    }
                }

                AnimatedVisibility(
                    visible = state.showGameOverDialog,
                    enter = fadeIn(),
                    exit = fadeOut(animationSpec = tween(durationMillis = 0))
                ) {
                    GameOverDialog(
                        state = state,
                        startGame = { onEvent(BubbleGameEvent.StartGame) },
                        ontoHome = ontoHome,
                        toSettings = toSettings
                    )
                }
            }

        }
    }

}

@Composable
fun PulsatingPauseIcon(isPaused: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "pause")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pauseAlpha"
    )

    Icon(
        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
        contentDescription = if (isPaused) "Resume" else "Pause",
        tint = MaterialTheme.colorScheme.primary.copy(alpha = if (isPaused) alpha else 1f)
    )
}


@Composable
fun GameOverDialog(
    state: GameState,
    startGame: () -> Unit,
    toSettings: () -> Unit,
    ontoHome: () -> Unit
) {
    val star = LocalMultiplyColors.current.star
    val isNewHighScore =
        state.score >= state.highScore && state.score != 0 && state.highScore != 0
    Dialog(onDismissRequest = { /* Dialog cannot be dismissed */ }) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surface)
                .then(if (state.score > 0) Modifier.confettiEffect() else Modifier)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 22.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(star.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.happy_mascot),
                        contentDescription = "Happy Mascot",
                        modifier = Modifier.size(86.dp)
                    )
                }

                Text(
                    text = if (state.score == 0) "Oh no!" else "Great job!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                AnimatedScoreText(score = state.score)

                if (isNewHighScore) NewHighScoreText()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    InfoPill(
                        label = "LEVEL",
                        value = state.selectedDifficulty.name.lowercase()
                            .replaceFirstChar { it.titlecase() }
                    )
                    InfoPill(
                        label = "BEST",
                        value = state.highScore.toString()
                    )
                }

                Spacer(Modifier.height(4.dp))

                DuoButton(
                    text = "Play Again",
                    onClick = startGame,
                    containerColor = LocalMultiplyColors.current.success,
                    contentColor = Color.White,
                    leading = Icons.Default.Refresh,
                    fontSize = 18,
                    height = 60.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DuoButton(
                        text = "Settings",
                        onClick = toSettings,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        leading = Icons.Default.Settings,
                        fontSize = 14,
                        height = 50.dp,
                        modifier = Modifier.weight(1f)
                    )
                    DuoButton(
                        text = "Home",
                        onClick = ontoHome,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        leading = Icons.Default.Home,
                        fontSize = 14,
                        height = 50.dp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoPill(label: String, value: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Text(
            text = value,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black
        )
    }
}


@Composable
fun AnimatedScoreText(score: Int) {
    var displayScore by remember { mutableIntStateOf(0) }
    LaunchedEffect(score) {
        displayScore = 0
        delay(300)
        while (displayScore < score) {
            displayScore++
            delay(50)
        }
    }

    Text(
        text = "$displayScore pts",
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Black,
        color = LocalMultiplyColors.current.star,
        textAlign = TextAlign.Center
    )
}

@Composable
fun NewHighScoreText() {
    val infiniteTransition = rememberInfiniteTransition(label = "hi")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hiScale"
    )

    Row(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(50))
            .background(LocalMultiplyColors.current.warning)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = "NEW HIGH SCORE!",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun HudBar(state: GameState) {
    val star = LocalMultiplyColors.current.star
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatChip(
            label = "Score",
            value = state.score.toString(),
            icon = Icons.Default.Star,
            containerColor = star.copy(alpha = 0.18f),
            contentColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        LivesDisplay(lives = state.lives)
        StatChip(
            label = "Best",
            value = state.highScore.toString(),
            icon = Icons.Default.EmojiEvents,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun LivesDisplay(lives: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(3) { index ->
            val isActive = index < lives
            val scale by animateFloatAsHeart(isActive)
            Icon(
                imageVector = if (isActive) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Life",
                tint = if (isActive) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.error.copy(alpha = 0.35f),
                modifier = Modifier
                    .size(22.dp)
                    .scale(scale)
            )
        }
    }
}

@Composable
private fun animateFloatAsHeart(isActive: Boolean): androidx.compose.runtime.State<Float> {
    val infiniteTransition = rememberInfiniteTransition(label = "heart")
    return infiniteTransition.animateFloat(
        initialValue = if (isActive) 1f else 0.85f,
        targetValue = if (isActive) 1.15f else 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartScale"
    )
}


@Composable
private fun AnswerButtons(state: GameState, submitAnswer: (choice: Int) -> Unit) {
    val palette = answerPalette()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        state.currentProblem?.choices?.chunked(2)?.forEachIndexed { rowIndex, rowChoices ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowChoices.forEachIndexed { colIndex, choice ->
                    val paletteIndex = (rowIndex * 2 + colIndex) % palette.size
                    val (container, content) = palette[paletteIndex]
                    DuoButton(
                        text = choice.toString(),
                        onClick = { submitAnswer(choice) },
                        containerColor = container,
                        contentColor = content,
                        enabled = !state.isPaused,
                        fontSize = 26,
                        height = 72.dp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun answerPalette(): List<Pair<Color, Color>> {
    val scheme = MaterialTheme.colorScheme
    return listOf(
        scheme.primaryContainer to scheme.onPrimaryContainer,
        scheme.secondaryContainer to scheme.onSecondaryContainer,
        scheme.tertiaryContainer to scheme.onTertiaryContainer,
        LocalMultiplyColors.current.successContainer to LocalMultiplyColors.current.onSuccessContainer
    )
}


@Composable
private fun ReadyPrompt(difficulty: String, highScore: Int, onStart: () -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.tertiary
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(primary.copy(alpha = 0.4f), Color.Transparent)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.math_mascot),
                contentDescription = null,
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(secondary.copy(alpha = 0.2f))
                    .padding(6.dp)
            )
        }
        Text(
            text = "Ready to play?",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Tap the right answer before the bubble lands. You've got 3 hearts — make them count!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            textAlign = TextAlign.Center
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InfoPill(label = "LEVEL", value = difficulty.lowercase().replaceFirstChar { it.titlecase() })
            InfoPill(label = "BEST", value = highScore.toString())
        }
        Spacer(Modifier.height(4.dp))
        DuoButton(
            text = "START",
            onClick = onStart,
            containerColor = LocalMultiplyColors.current.success,
            contentColor = Color.White,
            leading = Icons.Default.PlayArrow,
            fontSize = 22,
            height = 68.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun MathBubble(state: GameState, modifier: Modifier = Modifier) {
    state.currentProblem?.let { problem ->
        val transition = rememberInfiniteTransition(label = "bubble")
        val totalTime = 1f / state.gameSpeed

        val elapsed = (System.currentTimeMillis() - problem.startTime) / 1000f
        val progress = (1f - (elapsed / totalTime)).coerceIn(0f, 1f)

        val pulse by transition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bubblePulse"
        )

        val bubbleTop = MaterialTheme.colorScheme.primary
        val bubbleBottom = MaterialTheme.colorScheme.tertiary
        val ringColor = when {
            progress > 0.6f -> LocalMultiplyColors.current.success
            progress > 0.3f -> LocalMultiplyColors.current.warning
            else -> MaterialTheme.colorScheme.error
        }
        val glow = ringColor.copy(alpha = 0.35f)

        Box(
            modifier = modifier
                .size(220.dp)
                .offset { IntOffset(0, problem.position.toInt()) }
                .graphicsLayer { scaleX = pulse; scaleY = pulse },
            contentAlignment = Alignment.Center
        ) {
            val fontResolver = LocalFontFamilyResolver.current
            val density = LocalDensity.current
            val layoutDirection = LocalLayoutDirection.current
            val textMeasurer = TextMeasurer(
                defaultDensity = density,
                defaultFontFamilyResolver = fontResolver,
                defaultLayoutDirection = layoutDirection
            )
            val equationText = "${problem.num1} × ${problem.num2}"
            val equationTextStyle = MaterialTheme.typography.displayMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Black
            )
            val textLayoutResult = textMeasurer.measure(
                text = AnnotatedString(equationText),
                style = equationTextStyle,
            )
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.width / 2
                val center = Offset(radius, radius)

                // Outer glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(glow, Color.Transparent),
                        center = center,
                        radius = radius * 1.2f
                    ),
                    radius = radius * 1.05f,
                    center = center
                )

                // Bubble body — vertical gradient for depth
                drawCircle(
                    brush = Brush.verticalGradient(
                        colors = listOf(bubbleTop, bubbleBottom),
                        startY = 0f,
                        endY = size.height
                    ),
                    center = center,
                    radius = radius * 0.92f
                )

                // Highlight spot to make it feel spherical
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.55f), Color.Transparent),
                        center = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f),
                        radius = radius * 0.55f
                    ),
                    center = Offset(center.x - radius * 0.35f, center.y - radius * 0.35f),
                    radius = radius * 0.45f
                )

                // Timer ring — progress goes from full to empty
                val ringStroke = 8.dp.toPx()
                drawCircle(
                    color = ringColor.copy(alpha = 0.25f),
                    center = center,
                    radius = radius * 0.95f,
                    style = Stroke(width = ringStroke)
                )
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = Offset(center.x - radius * 0.95f, center.y - radius * 0.95f),
                    size = androidx.compose.ui.geometry.Size(radius * 0.95f * 2, radius * 0.95f * 2),
                    style = Stroke(width = ringStroke, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )

                drawText(
                    textMeasurer = textMeasurer,
                    text = equationText,
                    style = equationTextStyle,
                    topLeft = Offset(
                        center.x - textLayoutResult.size.width / 2,
                        center.y - textLayoutResult.size.height / 2
                    )
                )
            }
        }
    }
}

@Composable
private fun PauseOverlay(
    visible: Boolean,
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onQuit: () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(onClick = onResume),
            contentAlignment = Alignment.Center
        ) {
            PauseMenu(
                onResume = onResume,
                onRestart = onRestart,
                onQuit = onQuit
            )
        }
    }
}

@Composable
private fun PauseMenu(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onQuit: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(32.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.sleeping_mascot),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
            )
        }
        Text(
            text = "Paused",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Take a breath — your progress is safe.",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        DuoButton(
            text = "Keep Playing",
            onClick = onResume,
            containerColor = LocalMultiplyColors.current.success,
            contentColor = Color.White,
            leading = Icons.Default.PlayArrow,
            fontSize = 16,
            height = 54.dp,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DuoButton(
                text = "Restart",
                onClick = onRestart,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                leading = Icons.Default.Refresh,
                fontSize = 13,
                height = 48.dp,
                modifier = Modifier.weight(1f)
            )
            DuoButton(
                text = "Quit",
                onClick = onQuit,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                leading = Icons.Default.Home,
                fontSize = 13,
                height = 48.dp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


@PreviewScreenSizes
@Composable
private fun GameOverDialogPreview() {
    MultiplyTheme {
        GameOverDialog(state = GameState(score = 24, highScore = 18), toSettings = {}, ontoHome = {}, startGame = {})
    }
}


@PreviewScreenSizes
@Composable
private fun PreviewPauseMenu() {
    MultiplyTheme {
        PauseMenu(onQuit = {}, onResume = {}, onRestart = {})
    }
}
