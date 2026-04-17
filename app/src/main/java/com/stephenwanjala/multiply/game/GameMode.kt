package com.stephenwanjala.multiply.game

import androidx.activity.BackEventCompat
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stephenwanjala.multiply.R
import com.stephenwanjala.multiply.core.designsystem.component.AnimatedFloatingSymbolsBackground
import com.stephenwanjala.multiply.game.feat_bubblemode.CircleIconBadge
import com.stephenwanjala.multiply.game.feat_bubblemode.DuoButton
import com.stephenwanjala.multiply.game.models.BubbleMathDifficulty
import com.stephenwanjala.multiply.game.models.GameMode
import com.stephenwanjala.multiply.game.models.GameModeSaver
import com.stephenwanjala.multiply.game.models.ModeDifficulty
import com.stephenwanjala.multiply.game.models.ModeDifficultySaver
import com.stephenwanjala.multiply.game.models.QuizDifficulty
import com.stephenwanjala.multiply.ui.theme.LocalMultiplyColors
import com.stephenwanjala.multiply.ui.theme.MultiplyTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

@Composable
fun GameModeSelectionScreen(
    onConfirm: (GameMode, ModeDifficulty) -> Unit,
    lastHighScoreProvider: (GameMode, ModeDifficulty) -> Int = { _, _ -> 0 }
) {
    var selectedMode by rememberSaveable(stateSaver = GameModeSaver) {
        mutableStateOf<GameMode?>(null)
    }
    var selectedDifficulty by rememberSaveable(stateSaver = ModeDifficultySaver) {
        mutableStateOf<ModeDifficulty?>(null)
    }
    var backProgress by remember { mutableFloatStateOf(0f) }

    val modes = listOf(
        GameMode.BubbleMathBlitz(BubbleMathDifficulty.EASY),
        GameMode.QuizGenius(QuizDifficulty.BEGINNER)
    )

    if (selectedMode != null) {
        PredictiveBackHandler { progress: Flow<BackEventCompat> ->
            try {
                progress.collect { event -> backProgress = event.progress }
                selectedMode = null
                selectedDifficulty = null
                backProgress = 0f
            } catch (e: CancellationException) {
                backProgress = 0f
                throw e
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedFloatingSymbolsBackground(alpha = 0.3f)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .graphicsLayer(
                    scaleX = 1f - (backProgress * 0.05f),
                    scaleY = 1f - (backProgress * 0.05f),
                    translationX = backProgress * 100f,
                    alpha = 1f - (backProgress * 0.2f)
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            HeroHeader()

            AnimatedVisibility(
                visible = selectedMode == null,
                enter = fadeIn(tween(350)) + scaleIn(
                    initialScale = 0.9f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                ),
                exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.9f, animationSpec = tween(200)),
                label = "modeStep"
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    SectionLabel(eyebrow = "Step 1 of 2", title = "Pick your mode")
                    modes.forEach { mode ->
                        ModeCard(
                            mode = mode,
                            onClick = {
                                selectedMode = mode
                                selectedDifficulty = null
                            }
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = selectedMode != null,
                enter = fadeIn(tween(350, delayMillis = 100)) +
                        expandVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ),
                exit = fadeOut(tween(200)) + shrinkVertically(animationSpec = tween(200)),
                label = "difficultyStep"
            ) {
                selectedMode?.let { targetMode ->
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        BackChip(
                            label = "Change mode",
                            onClick = {
                                selectedMode = null
                                selectedDifficulty = null
                            }
                        )
                        SectionLabel(
                            eyebrow = "Step 2 of 2",
                            title = "Choose difficulty"
                        )

                        DifficultyList(
                            mode = targetMode,
                            selected = selectedDifficulty,
                            onSelect = { selectedDifficulty = it }
                        )

                        selectedDifficulty?.let { diff ->
                            val highScore = lastHighScoreProvider(targetMode, diff)
                            if (highScore > 0) {
                                HighScoreBadge(score = highScore)
                            }
                        }

                        Spacer(Modifier.height(6.dp))

                        DuoButton(
                            text = "Start ${targetMode.name}",
                            onClick = {
                                selectedDifficulty?.let { onConfirm(targetMode, it) }
                            },
                            enabled = selectedDifficulty != null,
                            containerColor = LocalMultiplyColors.current.success,
                            contentColor = Color.White,
                            leading = Icons.Default.PlayArrow,
                            fontSize = 18,
                            height = 62.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun HeroHeader() {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        primary.copy(alpha = 0.9f),
                        tertiary.copy(alpha = 0.85f)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.math_mascot),
                    contentDescription = null,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "LET'S PLAY",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp
                )
                Text(
                    text = "Choose your\nchallenge!",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    lineHeight = 28.sp
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(eyebrow: String, title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = eyebrow.uppercase(),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.4.sp
        )
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun BackChip(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ModeCard(
    mode: GameMode,
    onClick: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val spec = mode.cardSpec()
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "modeCardScale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(spec.accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = spec.mascotRes),
                contentDescription = mode.name,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = spec.eyebrow.uppercase(),
                color = spec.accent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.3.sp
            )
            Text(
                text = mode.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 19.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = spec.description,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontSize = 13.sp,
                lineHeight = 17.sp
            )
        }
        Spacer(Modifier.width(10.dp))
        CircleIconBadge(
            icon = Icons.Default.PlayArrow,
            backgroundColor = spec.accent,
            contentColor = Color.White,
            size = 40.dp,
            iconSize = 22.dp
        )
    }
}

@Composable
private fun DifficultyList(
    mode: GameMode,
    selected: ModeDifficulty?,
    onSelect: (ModeDifficulty) -> Unit
) {
    val options = difficultyOptionsFor(mode)
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEach { option ->
            val isSelected = selected == option.value
            DifficultyTile(option = option, isSelected = isSelected, onClick = { onSelect(option.value) })
        }
    }
}

@Composable
private fun DifficultyTile(
    option: DifficultyOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "diffTileScale"
    )
    val face = if (isSelected) option.accent else MaterialTheme.colorScheme.surface
    val titleColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
    val subColor =
        if (isSelected) Color.White.copy(alpha = 0.85f)
        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(RoundedCornerShape(20.dp))
            .background(face)
            .clickable {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) Color.White.copy(alpha = 0.22f)
                    else option.accent.copy(alpha = 0.18f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = option.emoji, fontSize = 26.sp)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.title,
                color = titleColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = option.subtitle,
                color = subColor,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun HighScoreBadge(score: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleIconBadge(
            icon = Icons.Default.EmojiEvents,
            backgroundColor = LocalMultiplyColors.current.star,
            contentColor = Color.White,
            size = 36.dp,
            iconSize = 20.dp
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "YOUR BEST",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "$score points",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

private data class ModeCardSpec(
    val eyebrow: String,
    val description: String,
    val accent: Color,
    val mascotRes: Int
)

@Composable
private fun GameMode.cardSpec(): ModeCardSpec = when (this) {
    is GameMode.BubbleMathBlitz -> ModeCardSpec(
        eyebrow = "Action",
        description = "Pop falling bubbles before they hit the floor. Fast and fun!",
        accent = MaterialTheme.colorScheme.primary,
        mascotRes = R.drawable.bubblemuscot
    )
    is GameMode.QuizGenius -> ModeCardSpec(
        eyebrow = "Focus",
        description = "Answer a set of questions at your own pace. Build your streak!",
        accent = MaterialTheme.colorScheme.tertiary,
        mascotRes = R.drawable.math_mascot
    )
}

private data class DifficultyOption(
    val value: ModeDifficulty,
    val emoji: String,
    val title: String,
    val subtitle: String,
    val accent: Color
)

@Composable
private fun difficultyOptionsFor(mode: GameMode): List<DifficultyOption> {
    val success = LocalMultiplyColors.current.success
    val warning = LocalMultiplyColors.current.warning
    val error = MaterialTheme.colorScheme.error
    val tertiary = MaterialTheme.colorScheme.tertiary
    return when (mode) {
        is GameMode.BubbleMathBlitz -> listOf(
            DifficultyOption(
                value = ModeDifficulty.Bubble(BubbleMathDifficulty.EASY),
                emoji = "🌱",
                title = "Easy",
                subtitle = "Slow pace • Small numbers",
                accent = success
            ),
            DifficultyOption(
                value = ModeDifficulty.Bubble(BubbleMathDifficulty.MEDIUM),
                emoji = "🔥",
                title = "Medium",
                subtitle = "Quick thinking • Mid numbers",
                accent = warning
            ),
            DifficultyOption(
                value = ModeDifficulty.Bubble(BubbleMathDifficulty.HARD),
                emoji = "⚡",
                title = "Hard",
                subtitle = "Lightning fast • Big numbers",
                accent = error
            )
        )
        is GameMode.QuizGenius -> QuizDifficulty.entries.map { level ->
            val (emoji, accent) = when (level) {
                QuizDifficulty.BEGINNER -> "🌱" to success
                QuizDifficulty.INTERMEDIATE -> "🔥" to warning
                QuizDifficulty.ADVANCED -> "⚡" to tertiary
                QuizDifficulty.EXPERT -> "💀" to error
            }
            val range = level.numberRange
            DifficultyOption(
                value = ModeDifficulty.Quiz(level),
                emoji = emoji,
                title = level.name.lowercase().replaceFirstChar { it.titlecase() },
                subtitle = "${level.questionCount} questions • ${range.first}-${range.last}",
                accent = accent
            )
        }
    }
}

private val GameMode.name: String
    get() = when (this) {
        is GameMode.BubbleMathBlitz -> "Bubble Blitz"
        is GameMode.QuizGenius -> "Quiz Genius"
    }

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun PreviewGameModes() {
    MultiplyTheme {
        GameModeSelectionScreen(onConfirm = { _, _ -> })
    }
}
