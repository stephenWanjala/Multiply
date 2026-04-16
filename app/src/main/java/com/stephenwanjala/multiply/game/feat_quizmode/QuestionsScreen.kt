package com.stephenwanjala.multiply.game.feat_quizmode

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Bottom
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Horizontal
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stephenwanjala.multiply.game.components.confettiEffect
import com.stephenwanjala.multiply.game.components.glowingOrbs
import com.stephenwanjala.multiply.game.components.neumorphicShadow
import com.stephenwanjala.multiply.game.models.hasLargeNumbers
import com.stephenwanjala.multiply.ui.theme.LocalMultiplyColors

const val EMOJI_DISPLAY_THRESHOLD = 12

@Composable
fun QuestionsScreen(
    viewModel: QuestionsViewModel,
    onClosePressed: () -> Unit
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is QuizEffect.ShowScoreToast -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                QuizEffect.NavigateHome -> onClosePressed()
            }
        }
    }

    if (state.showRecap) {
        RecapScreen(
            results = state.results,
            onClosePressed = onClosePressed,
            onRetry = { viewModel.onEvent(QuizEvent.RetryQuiz) }
        )
        return
    }
    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
            topBar = {
                QuestionsTopAppBar(
                    onClosePressed = onClosePressed,
                    currentQuestionIndex = state.currentQuestionIndex,
                    totalCount = state.questions.size
                )
            },
            bottomBar = {
                QuestionBottomBar(
                    shouldShowPreviousButton = state.currentQuestionIndex > 0,
                    shouldShowDoneButton = state.currentQuestionIndex == state.questions.lastIndex,
                    isNextButtonEnabled = state.selectedAnswer != null,
                    onPreviousPressed = { viewModel.onEvent(QuizEvent.PreviousQuestion) },
                    onNextPressed = { viewModel.onEvent(QuizEvent.NextQuestion) },
                    onDonePressed = { viewModel.onEvent(QuizEvent.SubmitAnswers) }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .glowingOrbs()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                QuestionContent(
                    state = state,
                    onAnswerSelected = { answer ->
                        viewModel.onEvent(QuizEvent.SelectAnswer(answer))
                    }
                )
            }
        }
    }
}

@Composable
private fun QuestionContent(
    state: QuestionsState,
    onAnswerSelected: (Int) -> Unit
) {
    val currentQuestion = state.currentQuestion
    val hasLargeNumbers = currentQuestion?.hasLargeNumbers() ?: false

    if (hasLargeNumbers) {
        LargeNumberQuestionContent(
            state = state,
            onAnswerSelected = onAnswerSelected
        )
    } else {
        RegularQuestionContent(
            state = state,
            onAnswerSelected = onAnswerSelected
        )
    }
}

@Composable
private fun RegularQuestionContent(
    state: QuestionsState,
    onAnswerSelected: (Int) -> Unit
) {
    val currentQuestion = state.currentQuestion
    val questionLength = currentQuestion?.question?.length ?: 0
    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "animatedOffset"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val questionPadding = if (questionLength > 10) 16.dp else 32.dp
        val questionTextStyle = if (questionLength > 10)
            MaterialTheme.typography.headlineMedium
        else
            MaterialTheme.typography.displayMedium

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = questionPadding)
                .neumorphicShadow(
                    lightColor = MaterialTheme.colorScheme.surface
                )
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = currentQuestion?.question,
                transitionSpec = {
                    (slideInHorizontally { height -> height } + fadeIn())
                        .togetherWith(slideOutHorizontally { height -> -height } + fadeOut())
                },
                label = "questionTextAnimation"
            ) { targetQuestionText ->
                Text(
                    text = targetQuestionText ?: "Loading...",
                    style = questionTextStyle.copy(
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.offset(y = (-animatedOffset).dp)
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(currentQuestion?.allAnswers?.size ?: 0) { index ->
                val answer = currentQuestion?.allAnswers?.get(index) ?: 0
                val isSelected = answer == state.selectedAnswer

                AnswerParticle(
                    number = answer,
                    isSelected = isSelected,
                    onClick = { onAnswerSelected(answer) }
                )
            }
        }

        ProgressIndicators(state)
    }
}

@Composable
private fun LargeNumberQuestionContent(
    state: QuestionsState,
    onAnswerSelected: (Int) -> Unit
) {
    val currentQuestion = state.currentQuestion
    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "animatedOffset"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = currentQuestion?.question ?: "Loading...",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .padding(20.dp)
                        .offset(y = (-animatedOffset).dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(currentQuestion?.allAnswers?.size ?: 0) { index ->
                    val answer = currentQuestion?.allAnswers?.get(index) ?: 0
                    val isSelected = answer == state.selectedAnswer

                    LargeNumberAnswerOption(
                        number = answer,
                        isSelected = isSelected,
                        onClick = { onAnswerSelected(answer) }
                    )
                }
            }
        }

        ProgressIndicators(state)
    }
}

@Composable
private fun LargeNumberAnswerOption(
    number: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "largeNumberAnswerColor"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = number.toString(),
            transitionSpec = {
                (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                        scaleIn(animationSpec = tween(220, delayMillis = 90), initialScale = 0.9f))
                    .togetherWith(
                        fadeOut(animationSpec = tween(90)) +
                                scaleOut(animationSpec = tween(90), targetScale = 0.9f)
                    )
            },
            label = "LargeNumberAnswerOptionTextAnimation"
        ) { answer ->
            Text(
                text = answer,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

        }
    }
}

@Composable
private fun ProgressIndicators(state: QuestionsState) {
    Row(
        modifier = Modifier.padding(top = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (state.questions.size <= EMOJI_DISPLAY_THRESHOLD) {
            repeat(state.questions.size) { index ->
                val isCurrent = index == state.currentQuestionIndex
                val indicator = when {
                    index < state.currentQuestionIndex -> "+"
                    isCurrent -> ">"
                    else -> "-"
                }
                val scale by animateFloatAsState(
                    targetValue = if (isCurrent) 1.5f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "indicatorScaleAnimation"
                )

                AnimatedContent(
                    targetState = indicator,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                                scaleIn(
                                    animationSpec = tween(220, delayMillis = 90),
                                    initialScale = 0.9f
                                ))
                            .togetherWith(
                                fadeOut(animationSpec = tween(90)) +
                                        scaleOut(animationSpec = tween(90), targetScale = 0.9f)
                            )
                    },
                    label = "indicatorTextAnimation",
                    modifier = Modifier.scale(scale)
                ) { targetIndicator ->
                    Text(
                        text = targetIndicator,
                        color = when (targetIndicator) {
                            "+" -> MaterialTheme.colorScheme.primary
                            ">" -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.outline
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            Text(
                text = "${state.currentQuestionIndex + 1} / ${state.questions.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
private fun AnswerParticle(
    number: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "answerParticleColor"
    )

    val animatedElevation by animateDpAsState(
        targetValue = if (isSelected) 16.dp else 8.dp,
        label = "answerParticleElevation"
    )

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(80.dp)
            .neumorphicShadow(
                offset = 8.dp,
                blurRadius = animatedElevation,
                shape = shape,
                inverted = true
            )
            .background(
                color = animatedColor,
                shape = shape
            )
            .clip(shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = number.toString(),
            transitionSpec = {
                (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                        scaleIn(animationSpec = tween(220, delayMillis = 90), initialScale = 0.9f))
                    .togetherWith(
                        fadeOut(animationSpec = tween(90)) +
                                scaleOut(animationSpec = tween(90), targetScale = 0.9f)
                    )
            },
            label = "answerTextAnimation"
        ) { targetNumberText ->
            Text(
                text = targetNumberText,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.scale(if (isSelected) 1.15f else 1f),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsTopAppBar(
    modifier: Modifier = Modifier,
    onClosePressed: () -> Unit,
    currentQuestionIndex: Int,
    totalCount: Int
) {
    Column(modifier = modifier.fillMaxWidth()) {

        CenterAlignedTopAppBar(
            title = {
                TopAppBarTitle(
                    questionIndex = currentQuestionIndex,
                    totalQuestionsCount = totalCount,
                )
            },
            actions = {
                IconButton(
                    onClick = onClosePressed,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface.copy(.67f)
                    )
                }
            }
        )

        val animatedProgress by animateFloatAsState(
            targetValue = if (totalCount > 0) (currentQuestionIndex + 1) / totalCount.toFloat() else 0f,
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
            label = "questionProgress"
        )
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        )
    }
}


@Composable
private fun TopAppBarTitle(
    questionIndex: Int,
    totalQuestionsCount: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = (questionIndex + 1).toString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = "of $totalQuestionsCount",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    }
}

@Composable
fun QuestionBottomBar(
    shouldShowPreviousButton: Boolean,
    shouldShowDoneButton: Boolean,
    isNextButtonEnabled: Boolean,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
    onDonePressed: () -> Unit
) {
    Surface(shadowElevation = 7.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.systemBars.only(Horizontal + Bottom))
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            if (shouldShowPreviousButton) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = onPreviousPressed,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "Previous")
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            if (shouldShowDoneButton) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = onDonePressed,
                    enabled = isNextButtonEnabled,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "Done")
                }
            } else {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = onNextPressed,
                    enabled = isNextButtonEnabled,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "Next")
                }
            }
        }
    }
}


@Composable
fun RecapScreen(
    results: List<GameResult>,
    onClosePressed: () -> Unit,
    onRetry: () -> Unit = {}
) {
    val correctCount = results.count { it.isCorrect }
    val totalCount = results.size
    val percentage = if (totalCount > 0) (correctCount * 100) / totalCount else 0
    val multiplyColors = LocalMultiplyColors.current

    val scoreColor = when {
        percentage >= 90 -> multiplyColors.success
        percentage >= 70 -> Color(0xFF2196F3)
        percentage >= 50 -> multiplyColors.star
        else -> multiplyColors.warning
    }

    val performanceTier = when {
        percentage == 100 -> PerformanceTier("Perfect Score!", "You nailed every single one!", 3)
        percentage >= 90 -> PerformanceTier("Outstanding!", "You're a math superstar!", 3)
        percentage >= 70 -> PerformanceTier("Great Job!", "You're getting really good at this!", 2)
        percentage >= 50 -> PerformanceTier("Nice Try!", "Keep going, you're improving!", 1)
        else -> PerformanceTier("Keep Practicing!", "Every attempt makes you stronger!", 0)
    }

    // Animated score counter
    val animatedPercentage by animateFloatAsState(
        targetValue = percentage.toFloat(),
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "score"
    )

    // Star bounce animation
    val starScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "starBounce"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .then(if (percentage >= 70) Modifier.confettiEffect(particleCount = 60) else Modifier),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Score hero section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, bottom = 16.dp)
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Circular score indicator
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(180.dp)
                            .scale(starScale)
                    ) {
                        Canvas(modifier = Modifier.size(180.dp)) {
                            // Background track
                            drawArc(
                                color = scoreColor.copy(alpha = 0.15f),
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Animated progress arc
                            drawArc(
                                color = scoreColor,
                                startAngle = -90f,
                                sweepAngle = animatedPercentage * 3.6f,
                                useCenter = false,
                                style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${animatedPercentage.toInt()}%",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = scoreColor
                            )
                            Text(
                                text = "$correctCount / $totalCount",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Star rating row
                    if (performanceTier.stars > 0) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            repeat(3) { index ->
                                val filled = index < performanceTier.stars
                                val delay = index * 200
                                val scale by animateFloatAsState(
                                    targetValue = if (filled) 1f else 0.6f,
                                    animationSpec = tween(
                                        durationMillis = 500,
                                        delayMillis = delay + 800,
                                        easing = FastOutSlowInEasing
                                    ),
                                    label = "star$index"
                                )
                                Icon(
                                    imageVector = if (filled) Icons.Filled.Star else Icons.Filled.StarBorder,
                                    contentDescription = null,
                                    tint = if (filled) multiplyColors.star else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .scale(scale)
                                )
                            }
                        }
                    }

                    // Performance message
                    Text(
                        text = performanceTier.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = scoreColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = performanceTier.subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Stats row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        label = "Correct",
                        value = "$correctCount",
                        color = multiplyColors.success,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Wrong",
                        value = "${totalCount - correctCount}",
                        color = multiplyColors.wrongAnswer,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        label = "Accuracy",
                        value = "$percentage%",
                        color = scoreColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Action buttons
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onClosePressed,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Home", fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onRetry,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = scoreColor
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Try Again", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Section header for results
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question Review",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Result cards
            itemsIndexed(results) { index, result ->
                QuestionRecapItem(
                    questionNumber = index + 1,
                    result = result,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

private data class PerformanceTier(val title: String, val subtitle: String, val stars: Int)

@Composable
private fun StatCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = color.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun QuestionRecapItem(
    questionNumber: Int,
    result: GameResult,
    modifier: Modifier = Modifier
) {
    val multiplyColors = LocalMultiplyColors.current
    val statusColor = if (result.isCorrect) multiplyColors.success else multiplyColors.wrongAnswer
    val containerColor = if (result.isCorrect)
        multiplyColors.success.copy(alpha = 0.06f)
    else
        multiplyColors.wrongAnswer.copy(alpha = 0.06f)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = statusColor.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator circle
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(statusColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (result.isCorrect) Icons.Filled.Check else Icons.Filled.Close,
                    contentDescription = if (result.isCorrect) "Correct" else "Wrong",
                    tint = statusColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Q$questionNumber",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = result.question,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!result.isCorrect) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Your answer: ${result.userAnswer}",
                            style = MaterialTheme.typography.bodySmall,
                            color = multiplyColors.wrongAnswer,
                            textDecoration = TextDecoration.LineThrough
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Correct: ${result.correctAnswer}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = multiplyColors.success
                        )
                    }
                }
            }
        }
    }
}
