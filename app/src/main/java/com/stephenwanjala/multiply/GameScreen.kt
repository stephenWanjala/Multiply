package com.stephenwanjala.multiply

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun GameScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val screenSize = currentWindowSize()
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(screenSize) {
        viewModel.updateScreenHeight(screenSize.height.toFloat())
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (!state.gameActive) {
            StartButton(viewModel)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameHeader(state = state)

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
            GameOverDialog(state = state, startGame = {viewModel.startGame()})
        }
    }
}

@Composable
private fun GameOverDialog(state: GameState, startGame: () -> Unit) {
    AlertDialog(
        onDismissRequest = { /* Dialog cannot be dismissed */ },
        title = {
            Text(
                text = "Game Over!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Final Score: ${state.score}",
                    style = MaterialTheme.typography.titleLarge
                )
                if (state.score >= state.highScore) {
                    Text(
                        text = "🎉 New High Score! 🎉",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = "Previous High Score: ${state.highScore}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { startGame() },
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Play Again",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}


@Composable
private fun GameHeader(state: GameState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Score: ${state.score}",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "High Score: ${state.highScore}",
            style = MaterialTheme.typography.titleMedium
        )
        Row {
            repeat(state.lives) {
                Text(
                    text = "❤️",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun FallingProblem(state: GameState) {
    state.currentProblem?.let { problem ->
        Text(
            text = "${problem.num1} × ${problem.num2}",
            modifier = Modifier
                .offset(y = problem.position.dp)
                .fillMaxWidth(),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displayMedium
        )
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
fun StartButton(viewModel: GameViewModel) {
    val transition = rememberInfiniteTransition(label = "gradientAnimation")

    val gradientOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientOffset"
    )

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary,
            MaterialTheme.colorScheme.tertiary
        ),
        start = Offset(gradientOffset, 0f),
        end = Offset(gradientOffset + 300f, 300f) // Diagonal movement
    )

    Box(
        modifier = Modifier
            .padding(16.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(gradientBrush)
            .clickable { viewModel.startGame() }
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Start Game",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
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