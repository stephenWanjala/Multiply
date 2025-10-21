package com.stephenwanjala.multiply.game.feat_quizmode

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stephenwanjala.multiply.game.components.glowingOrbs
import com.stephenwanjala.multiply.game.components.neumorphicShadow
import com.stephenwanjala.multiply.game.models.hasLargeNumbers

//TODO("TO BE REMOVED")
const val EMOJI_DISPLAY_THRESHOLD = 12

@Composable
fun QuestionsScreen(viewModel: QuestionsViewModel, onClosePressed: () -> Unit) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    if (state.showRecap) {
        RecapScreen(results = state.results, onClosePressed = onClosePressed)
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
                    onPreviousPressed = { viewModel.onAction(QuestionAction.PreviousQuestion) },
                    onNextPressed = { viewModel.onAction(QuestionAction.NextQuestion) },
                    onDonePressed = { viewModel.onAction(QuestionAction.SubmitAnswer) }
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
                        viewModel.onAction(QuestionAction.SelectAnswer(answer))
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

    // Use different layouts based on number size
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
        // Floating question card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp)
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
                    text = targetQuestionText ?: "🎲 Loading...",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.offset(y = (-animatedOffset).dp)
                )
            }
        }

        // Regular grid for normal numbers
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
        // Compact question display for large numbers
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
                    text = currentQuestion?.question ?: "🎲 Loading...",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(20.dp).offset(y = (-animatedOffset).dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Single column for large numbers - easier to read
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
                    .togetherWith(fadeOut(animationSpec = tween(90)) +
                            scaleOut(animationSpec = tween(90), targetScale = 0.9f))
            },
            label = "LargeNumberAnswerOptionTextAnimation"
        ){answer->
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
                val emoji = when {
                    index < state.currentQuestionIndex -> "✅"
                    isCurrent -> "🎯"
                    else -> "➖"
                }
                val scale by animateFloatAsState(
                    targetValue = if (isCurrent) 1.5f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "emojiScaleAnimation"
                )

                AnimatedContent(
                    targetState = emoji,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                                scaleIn(animationSpec = tween(220, delayMillis = 90), initialScale = 0.9f))
                            .togetherWith(fadeOut(animationSpec = tween(90)) +
                                    scaleOut(animationSpec = tween(90), targetScale = 0.9f))
                    },
                    label = "emojiTextAnimation",
                    modifier = Modifier.scale(scale)
                ) { targetEmoji ->
                    Text(text = targetEmoji)
                }
            }
        } else {
            val currentEmoji = "🎯"
            val scale by animateFloatAsState(
                targetValue = 1.5f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "currentEmojiScale"
            )
            AnimatedContent(
                targetState = currentEmoji,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                            scaleIn(animationSpec = tween(220, delayMillis = 90), initialScale = 0.9f))
                        .togetherWith(fadeOut(animationSpec = tween(90)) +
                                scaleOut(animationSpec = tween(90), targetScale = 0.9f))
                },
                label = "currentEmojiAnimation",
                modifier = Modifier.scale(scale)
            ) { targetEmoji ->
                Text(text = targetEmoji)
            }
            Spacer(modifier = Modifier.width(8.dp))
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
                    .togetherWith(fadeOut(animationSpec = tween(90)) +
                            scaleOut(animationSpec = tween(90), targetScale = 0.9f))
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


@Composable
private fun AnswerOption(
    answer: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onSelected: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    OutlinedButton(
        onClick = onSelected,
        modifier = modifier
            .height(80.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        border = ButtonDefaults.outlinedButtonBorder(enabled = isSelected)
    ) {
        Text(
            text = answer.toString(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
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
                // Since we're not using a Material component but we implement our own bottom bar,
                // we will also need to implement our own edge-to-edge support. Similar to the
                // NavigationBar, we add the horizontal and bottom padding if it hasn't been consumed yet.
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecapScreen(results: List<GameResult>, onClosePressed: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Quiz Recap", fontWeight = FontWeight.Bold) }, actions = {
                    Button(onClick = onClosePressed) {
                        Text("Home")
                    }
                })
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                itemsIndexed(results) { index, result ->
                    QuestionRecapItem(questionNumber = index + 1, result = result)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun AnswerRow(answer: Int, isUserAnswer: Boolean, isCorrect: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = answer.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = when {
                isCorrect -> Color(0xFF4CAF50)
                isUserAnswer -> Color.Gray
                else -> MaterialTheme.colorScheme.onSurface
            },
            textDecoration = if (isUserAnswer && !isCorrect) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier.weight(1f)
        )
        if (isCorrect) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Correct Answer",
                tint = Color(0xFF4CAF50)
            )
        } else if (isUserAnswer) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Wrong Answer",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun QuestionRecapItem(questionNumber: Int, result: GameResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Question $questionNumber: ${result.question}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            AnswerRow(
                answer = result.correctAnswer,
                isUserAnswer = result.userAnswer == result.correctAnswer,
                isCorrect = true
            )
            if (!result.isCorrect) {
                AnswerRow(
                    answer = result.userAnswer,
                    isUserAnswer = true,
                    isCorrect = false
                )
            }
        }
    }
}