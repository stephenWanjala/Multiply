package com.stephenwanjala.multiply.game.feat_bubblemode

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteTransition
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stephenwanjala.multiply.R
import com.stephenwanjala.multiply.game.components.FloatingSymbols
import com.stephenwanjala.multiply.game.components.confettiEffect
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

    LaunchedEffect(screenSize) {
        viewModel.updateScreenHeight(screenSize.height.toFloat())
    }
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(
                text = "${stringResource(R.string.app_name)} ~ ${state.selectedDifficulty.name}",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
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
                        if (state.isPaused) viewModel.resumeGame()
                        else viewModel.pauseGame()
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
                            Color(0xFF87CEEB),
                            MaterialTheme.colorScheme.background,
                            Color(0xFF4682B4)
                        )
                    )
                )
        ) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                if (!state.gameActive) {
                    AnimatedStartButton(viewModel)
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedGameHeader(state = state)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(bottom = 80.dp)
                                .onSizeChanged { layoutSize ->
                                    viewModel.updateGameAreaHeight(layoutSize.height.toFloat())
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            FloatingSymbols()
                            if ((state.currentProblem?.position ?: 0f) <= state.safeAreaHeight) {
                                MathBubble(state = state)
                            }
                            androidx.compose.animation.AnimatedVisibility(visible = state.isPaused) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                        .clickable { viewModel.resumeGame() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Floating island background
                                    FloatingIslandPauseMenu(
                                        onResume = { viewModel.resumeGame() },
                                        onRestart = { viewModel.reStartGame() },
                                        onQuit = ontoHome
                                    )

                                    // Animated sleeping zzz's
                                    ZzzAnimation()

                                    // Floating clouds
                                    ParallaxClouds()
                                }
                            }
                        }

                        AnswerButtons(state = state, submitAnswer = {
                            viewModel.submitAnswer(it)
                        })
                    }
                }

                // Show game over dialog when needed
                AnimatedVisibility(
                    visible = viewModel.showGameOverDialog,
                    enter = fadeIn(),
                    exit = fadeOut(animationSpec = tween(durationMillis = 0))
                ) {
                    GameOverDialog(
                        state = state,
                        startGame = { viewModel.startGame() },
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
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
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
    Dialog(
        onDismissRequest = { /* Dialog cannot be dismissed */ }
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(MaterialTheme.colorScheme.background, Color(0xFF87CEEB)),
                    )
                )
                .padding(16.dp)
                .then(if (state.score > 0) Modifier.confettiEffect() else Modifier)
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Game Over!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                // Score & Mascot
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AnimatedScoreText(score = state.score)
                    if (state.score >= state.highScore && state.score != 0 && state.highScore != 0) {
                        NewHighScoreText()
                    }
                    Text(
                        text = "Game Level :${state.selectedDifficulty.name}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Previous Best: ${state.highScore}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Image(
                        painter = painterResource(id = R.drawable.happy_mascot),
                        contentDescription = "Happy Mascot",
                        modifier = Modifier.size(100.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    PulsatingButton(
                        onClick = toSettings,
                        text = "Settings",
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    PulsatingButton(
                        onClick = ontoHome,
                        text = "Quit",
                    )
                }

                PulsatingButton(
                    onClick = startGame,
                    text = "Play Again!",
                    icon = Icons.Default.Refresh,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }
    }
}


@Composable
fun AnimatedScoreText(score: Int) {
    var displayScore by remember { mutableIntStateOf(0) }
    val key by remember { mutableIntStateOf(0) }

    LaunchedEffect(key) {
        displayScore = 0
        delay(500) // Wait for dialog animation
        while (displayScore < score) {
            displayScore++
            delay(50)
        }
    }

    Text(
        text = "Final Score: $displayScore",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = Color.Yellow,
        textAlign = TextAlign.Center
    )
}

@Composable
fun NewHighScoreText() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Text(
        text = "🎉 New High Score! 🎉",
        color = Color(0xFFFFA500),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.scale(scale)
    )
}

@Composable
fun PulsatingButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    text: String
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF32CD32))
    ) {
        Row(
            modifier = Modifier.wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


@Composable
fun AnimatedGameHeader(state: GameState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScoreDisplay(score = state.score, title = "Score")
        LivesDisplay(lives = state.lives)
        ScoreDisplay(score = state.highScore, title = "High Score")
    }
}

@Composable
fun ScoreDisplay(score: Int, title: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
        )
        Text(
            text = score.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun LivesDisplay(lives: Int) {
    Row {
        repeat(3) { index ->
            val isActive = index < lives
            Icon(
                imageVector = if (isActive) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Life",
                tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                    alpha = 0.3f
                ),
                modifier = Modifier
                    .size(32.dp)
                    .scale(if (isActive) 1.2f else 1f)
                    .padding(horizontal = 2.dp)
            )
        }
    }
}


@Composable
private fun AnswerButtons(state: GameState, submitAnswer: (choice: Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        state.currentProblem?.choices?.chunked(2)?.forEach { rowChoices ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowChoices.forEach { choice ->
                    Button(
                        onClick = { submitAnswer(choice) },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        enabled = !state.isPaused,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = choice.toString(),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


@Composable
fun AnimatedStartButton(viewModel: GameViewModel) {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Button(
        onClick = { viewModel.startGame() },
        modifier = Modifier
            .scale(scale)
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium
//        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA500))
    ) {
        Text(
            "Start Game",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}


@Composable
fun MathBubble(state: GameState) {
    state.currentProblem?.let { problem ->
        val transition = rememberInfiniteTransition(label = "gradientTransition")
        val totalTime = 1f / state.gameSpeed  // Calculate total time based on game speed

        val elapsed = (System.currentTimeMillis() - problem.startTime) / 1000f
        val progress = (1f - (elapsed / totalTime)).coerceIn(0f, 1f)

        // Pulse animation for time remaining
        val pulseAlpha by transition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseAlpha"
        )

        Box(
            modifier = Modifier
                .size(210.dp)
                .offset { IntOffset(0, problem.position.toInt()) },
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
            val pulseColor = MaterialTheme.colorScheme.error.copy(alpha = pulseAlpha)
            val circleColor = MaterialTheme.colorScheme.primaryContainer
            val equationTextStyle = MaterialTheme.typography.displayMedium.copy(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )
            val textLayoutResult = textMeasurer.measure(
                text = AnnotatedString("${problem.num1} X ${problem.num2}"),
                style = MaterialTheme.typography.displayMedium,

                )
            Canvas(modifier = Modifier.fillMaxSize()) {
                val circleRadius = size.width / 2
                val circleCenter = Offset(circleRadius, circleRadius)

                // Draw background bubble
                drawCircle(
                    color = circleColor,
                    center = circleCenter,
                    radius = circleRadius
                )

                // Draw time remaining pulse
                drawCircle(
                    color = pulseColor,
                    center = circleCenter,
                    radius = circleRadius * progress,
                    style = Stroke(width = 4.dp.toPx())
                )

                // Draw equation text
                drawText(
                    textMeasurer = textMeasurer,
                    text = "${problem.num1} × ${problem.num2}",
                    style = equationTextStyle,
                    topLeft = Offset(
                        circleCenter.x - textLayoutResult.size.width / 2,
                        circleCenter.y - textLayoutResult.size.height / 2
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FloatingIslandPauseMenu(
    onResume: () -> Unit,
    onRestart: () -> Unit,
    onQuit: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val imageBitmap = ImageBitmap.imageResource(R.drawable.sleeping_mascot)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationZ = floatOffset * 0.2f
                translationY = floatOffset
            }
            .drawBehind {
//                Island
                drawPath(
                    path = Path().apply {
                        moveTo(0f, size.height)
                        quadraticTo(size.width / 2, size.height - 150f, size.width, size.height)
                    },
                    color = Color(0xFF4CAF50)
                )

                // Draw sleeping mascot
                drawImage(
                    image = imageBitmap,
                    dstSize = IntSize(150, 150),
                    dstOffset = IntOffset(size.width.toInt() / 2 - 75, 50)
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))
        // Pause menu buttons
        FlowColumn(
            modifier = Modifier
                .wrapContentSize(),
//                .background(Color.Blue.copy(alpha = 0.2f)), // For Debugging TF😂😂😒 background color
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HoverButton(
                text = "Keep Playing",
                icon = Icons.Default.PlayArrow,
                onClick = onResume
            )

            HoverButton(
                text = "Start Fresh",
                icon = Icons.Default.Refresh,
                onClick = onRestart
            )

            HoverButton(
                text = "Quit to Home",
                icon = Icons.Default.Home,
                onClick = onQuit
            )
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun ZzzAnimation() {
    val symbols = listOf("💤", "😴", "✨", "⏸️")
    val infiniteTransition = rememberInfiniteTransition()

    repeat(8) { index ->
        val xOffset by infiniteTransition.animateFloat(
            initialValue = -50f,
            targetValue = 50f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000 + index * 500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        val yOffset by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -100f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500 + index * 500, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )

        Text(
            text = symbols[index % symbols.size],
            modifier = Modifier
//                .offset(xOffset.dp, yOffset.dp)
                .graphicsLayer {
                    translationX = xOffset
                    translationY = yOffset
                    this.alpha = this.translationX
                },
            fontSize = 24.sp,
            color = Color.White.copy(alpha = alpha)
        )
    }
}

@Composable
private fun ParallaxClouds() {
    val infiniteTransition = rememberInfiniteTransition()

    // Back layer clouds
    CloudLayer(
        speedMultiplier = 0.5f,
        scale = 0.8f,
        alpha = 0.4f,
        infiniteTransition = infiniteTransition
    )

    // Front layer clouds
    CloudLayer(
        speedMultiplier = 1.2f,
        scale = 1f,
        alpha = 0.7f,
        infiniteTransition = infiniteTransition
    )
}

@Composable
private fun CloudLayer(
    speedMultiplier: Float,
    scale: Float,
    alpha: Float,
    infiniteTransition: InfiniteTransition
) {
    repeat(5) { index ->
        val xOffset by infiniteTransition.animateFloat(
            initialValue = -200f,
            targetValue = 200f,
            animationSpec = infiniteRepeatable(
                animation = tween((3000 / speedMultiplier).toInt(), easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Image(
            painter = painterResource(R.drawable.ic_cloud),
            contentDescription = null,
            modifier = Modifier
                .scale(scale)
                .graphicsLayer {
                    this.alpha = alpha
                    translationX = xOffset * (index + 1)
                }
                .offset(y = (index * 50).dp)
        )
    }
}

@Composable
fun HoverButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .graphicsLayer {
                scaleX = if (isHovered) 1.1f else scale
                scaleY = if (isHovered) 1.1f else scale
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()

                        when (event.type) {
                            PointerEventType.Enter -> {
                                isHovered = true
                            }

                            PointerEventType.Exit -> {
                                isHovered = false
                            }
                        }
                    }
                }
            },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFD700),
            contentColor = Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, fontWeight = FontWeight.Bold)
        }
    }
}

@PreviewScreenSizes
@Composable
private fun GameOverDialogPreview() {
    MultiplyTheme {
        GameOverDialog(state = GameState(), toSettings = {}, ontoHome = {}, startGame = {})
    }
}


@PreviewScreenSizes
@Composable
private fun PreviewFloatingIslandPauseMenu() {
    MultiplyTheme {
        FloatingIslandPauseMenu(onQuit = {}, onResume = {}, onRestart = {})
    }
}
