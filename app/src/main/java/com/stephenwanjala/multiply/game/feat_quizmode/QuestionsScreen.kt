package com.stephenwanjala.multiply.game.feat_quizmode

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stephenwanjala.multiply.game.components.confettiEffect
import com.stephenwanjala.multiply.game.feat_bubblemode.DuoButton
import com.stephenwanjala.multiply.game.feat_bubblemode.darken
import com.stephenwanjala.multiply.game.models.hasLargeNumbers
import com.stephenwanjala.multiply.ui.theme.LocalMultiplyColors

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
                is QuizEffect.ShowScoreToast ->
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
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

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            QuizHeader(
                currentIndex = state.currentQuestionIndex,
                totalCount = state.questions.size,
                onClose = onClosePressed
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                QuestionContent(
                    state = state,
                    onAnswerSelected = { answer ->
                        viewModel.onEvent(QuizEvent.SelectAnswer(answer))
                    }
                )
            }

            QuizBottomBar(
                showPrevious = state.currentQuestionIndex > 0,
                isLast = state.currentQuestionIndex == state.questions.lastIndex,
                ctaEnabled = state.selectedAnswer != null,
                onPrevious = { viewModel.onEvent(QuizEvent.PreviousQuestion) },
                onNext = { viewModel.onEvent(QuizEvent.NextQuestion) },
                onDone = { viewModel.onEvent(QuizEvent.SubmitAnswers) }
            )
        }
    }
}

@Composable
private fun QuizHeader(
    currentIndex: Int,
    totalCount: Int,
    onClose: () -> Unit
) {
    val progress by animateFloatAsState(
        targetValue = if (totalCount > 0) (currentIndex + 1) / totalCount.toFloat() else 0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "quizProgress"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClick = onClose),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(14.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                    .height(14.dp)
                    .clip(RoundedCornerShape(50))
                    .background(LocalMultiplyColors.current.success)
            )
        }

        Text(
            text = "${(currentIndex + 1).coerceAtMost(totalCount.coerceAtLeast(1))}/${totalCount.coerceAtLeast(1)}",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun QuestionContent(
    state: QuestionsState,
    onAnswerSelected: (Int) -> Unit
) {
    val currentQuestion = state.currentQuestion ?: return
    val useColumnLayout = currentQuestion.hasLargeNumbers()

    QuestionCard(question = currentQuestion.question)

    if (useColumnLayout) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(currentQuestion.allAnswers) { _, answer ->
                AnswerOption(
                    text = answer.toString(),
                    isSelected = answer == state.selectedAnswer,
                    onClick = { onAnswerSelected(answer) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(currentQuestion.allAnswers) { _, answer ->
                AnswerOption(
                    text = answer.toString(),
                    isSelected = answer == state.selectedAnswer,
                    onClick = { onAnswerSelected(answer) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                )
            }
        }
    }
}

@Composable
private fun QuestionCard(question: String) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        primary.copy(alpha = 0.92f),
                        tertiary.copy(alpha = 0.88f)
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "SOLVE THIS",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.4.sp
            )
            Spacer(Modifier.height(10.dp))
            AnimatedContent(
                targetState = question,
                transitionSpec = {
                    (slideInHorizontally { it } + fadeIn())
                        .togetherWith(slideOutHorizontally { -it } + fadeOut())
                },
                label = "questionText"
            ) { text ->
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 52.sp
                )
            }
        }
    }
}

@Composable
private fun AnswerOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    val accent = MaterialTheme.colorScheme.primary
    val face = if (isSelected) accent.copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface
    val borderColor = if (isSelected) accent else MaterialTheme.colorScheme.outlineVariant
    val stripe = if (isSelected) accent.darken(0.28f) else MaterialTheme.colorScheme.outlineVariant
    val textColor = if (isSelected) accent else MaterialTheme.colorScheme.onSurface

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "answerScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .background(stripe.copy(alpha = if (isSelected) 1f else 0.35f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(face)
                .border(2.dp, borderColor, RoundedCornerShape(18.dp))
                .clickable {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    onClick()
                }
                .padding(vertical = 18.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = text,
                transitionSpec = {
                    (fadeIn(tween(180)) + scaleIn(tween(180), initialScale = 0.92f))
                        .togetherWith(fadeOut(tween(120)) + scaleOut(tween(120), targetScale = 0.92f))
                },
                label = "answerText"
            ) { value ->
                Text(
                    text = value,
                    color = textColor,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun QuizBottomBar(
    showPrevious: Boolean,
    isLast: Boolean,
    ctaEnabled: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onDone: () -> Unit
) {
    Surface(
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (showPrevious) {
                DuoButton(
                    text = "Back",
                    onClick = onPrevious,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16,
                    height = 56.dp,
                    modifier = Modifier.weight(1f)
                )
            }
            DuoButton(
                text = if (isLast) "Finish" else "Check",
                onClick = if (isLast) onDone else onNext,
                enabled = ctaEnabled,
                containerColor = LocalMultiplyColors.current.success,
                contentColor = Color.White,
                leading = Icons.Default.Check,
                fontSize = 18,
                height = 56.dp,
                modifier = Modifier.weight(if (showPrevious) 1.4f else 1f)
            )
        }
    }
}

@Composable
private fun RecapScreen(
    results: List<GameResult>,
    onClosePressed: () -> Unit,
    onRetry: () -> Unit
) {
    val correctCount = results.count { it.isCorrect }
    val totalCount = results.size
    val percentage = if (totalCount > 0) (correctCount * 100) / totalCount else 0
    val multiplyColors = LocalMultiplyColors.current

    val scoreColor = when {
        percentage >= 90 -> multiplyColors.success
        percentage >= 70 -> MaterialTheme.colorScheme.primary
        percentage >= 50 -> multiplyColors.star
        else -> multiplyColors.warning
    }

    val tier = when {
        percentage == 100 -> RecapTier("Perfect!", "You aced every question.", 3)
        percentage >= 90 -> RecapTier("Outstanding!", "You're a math superstar.", 3)
        percentage >= 70 -> RecapTier("Great Job!", "You're getting really good at this.", 2)
        percentage >= 50 -> RecapTier("Nice Try!", "Keep going — you're improving.", 1)
        else -> RecapTier("Keep Practicing!", "Every attempt makes you stronger.", 0)
    }

    val animatedPercentage by animateFloatAsState(
        targetValue = percentage.toFloat(),
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "score"
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .then(if (percentage >= 70) Modifier.confettiEffect(particleCount = 60) else Modifier)
                .windowInsetsPadding(WindowInsets.statusBars),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                RecapHero(
                    animatedPercentage = animatedPercentage,
                    correctCount = correctCount,
                    totalCount = totalCount,
                    tier = tier,
                    scoreColor = scoreColor,
                    starColor = multiplyColors.star
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RecapStat(
                        label = "Correct",
                        value = "$correctCount",
                        accent = multiplyColors.success,
                        modifier = Modifier.weight(1f)
                    )
                    RecapStat(
                        label = "Wrong",
                        value = "${totalCount - correctCount}",
                        accent = multiplyColors.wrongAnswer,
                        modifier = Modifier.weight(1f)
                    )
                    RecapStat(
                        label = "Accuracy",
                        value = "$percentage%",
                        accent = scoreColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DuoButton(
                        text = "Home",
                        onClick = onClosePressed,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        leading = Icons.AutoMirrored.Default.ArrowBack,
                        fontSize = 16,
                        height = 58.dp,
                        modifier = Modifier.weight(1f)
                    )
                    DuoButton(
                        text = "Try Again",
                        onClick = onRetry,
                        containerColor = multiplyColors.success,
                        contentColor = Color.White,
                        leading = Icons.Default.Refresh,
                        fontSize = 16,
                        height = 58.dp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QUESTION REVIEW",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.4.sp
                    )
                }
            }

            itemsIndexed(results) { index, result ->
                RecapRow(
                    number = index + 1,
                    result = result,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }

            item {
                Spacer(
                    modifier = Modifier
                        .height(24.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                )
            }
        }
    }
}

@Composable
private fun RecapHero(
    animatedPercentage: Float,
    correctCount: Int,
    totalCount: Int,
    tier: RecapTier,
    scoreColor: Color,
    starColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp, bottom = 8.dp)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(180.dp)
        ) {
            Canvas(modifier = Modifier.size(180.dp)) {
                drawArc(
                    color = scoreColor.copy(alpha = 0.15f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                )
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
                    color = scoreColor,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "$correctCount of $totalCount",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        if (tier.stars > 0) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                repeat(3) { index ->
                    val filled = index < tier.stars
                    val scale by animateFloatAsState(
                        targetValue = if (filled) 1f else 0.6f,
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = index * 200 + 800,
                            easing = FastOutSlowInEasing
                        ),
                        label = "star$index"
                    )
                    Icon(
                        imageVector = if (filled) Icons.Filled.Star else Icons.Filled.StarBorder,
                        contentDescription = null,
                        tint = if (filled) starColor else MaterialTheme.colorScheme.outline,
                        modifier = Modifier
                            .size(40.dp)
                            .scale(scale)
                    )
                }
            }
        }

        Text(
            text = tier.title,
            color = scoreColor,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = tier.subtitle,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RecapStat(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(accent.copy(alpha = 0.12f))
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = accent,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label.uppercase(),
            color = accent.copy(alpha = 0.85f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun RecapRow(
    number: Int,
    result: GameResult,
    modifier: Modifier = Modifier
) {
    val multiplyColors = LocalMultiplyColors.current
    val statusColor = if (result.isCorrect) multiplyColors.success else multiplyColors.wrongAnswer
    val container = statusColor.copy(alpha = 0.08f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(container)
            .border(1.dp, statusColor.copy(alpha = 0.22f), RoundedCornerShape(18.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(statusColor.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (result.isCorrect) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Q$number",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = result.question,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
            if (!result.isCorrect) {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "You: ${result.userAnswer}",
                        color = multiplyColors.wrongAnswer,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.LineThrough
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Answer: ${result.correctAnswer}",
                        color = multiplyColors.success,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

private data class RecapTier(val title: String, val subtitle: String, val stars: Int)
