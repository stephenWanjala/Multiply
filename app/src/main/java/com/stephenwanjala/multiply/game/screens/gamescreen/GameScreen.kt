package com.stephenwanjala.multiply.game.screens.gamescreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
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
                text = stringResource(R.string.app_name),
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
                        }

                        AnswerButtons(state = state, submitAnswer = {
                            viewModel.submitAnswer(it)
                        })
                    }
                }

                // Show game over dialog when needed
                AnimatedVisibility(visible = viewModel.showGameOverDialog) {
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
                    horizontalArrangement =Arrangement.Center
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

@PreviewScreenSizes
@Composable
private fun GameOverDialogPreview() {
    MultiplyTheme {
        GameOverDialog(state = GameState(), toSettings = {}, ontoHome = {}, startGame = {})
    }
}