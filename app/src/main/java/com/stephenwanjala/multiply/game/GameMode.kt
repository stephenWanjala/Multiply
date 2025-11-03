package com.stephenwanjala.multiply.game

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stephenwanjala.multiply.R
import com.stephenwanjala.multiply.game.components.animatedBackground
import com.stephenwanjala.multiply.game.components.glowingOrbs
import com.stephenwanjala.multiply.game.components.neumorphicShadow
import com.stephenwanjala.multiply.game.components.repeatLiquidBackground
import com.stephenwanjala.multiply.game.models.*
import com.stephenwanjala.multiply.ui.theme.MultiplyTheme
import kotlinx.coroutines.launch

@Composable
fun GameModeSelectionScreen(
    onConfirm: (GameMode, ModeDifficulty) -> Unit,
    lastHighScoreProvider: (GameMode, ModeDifficulty) -> Int = { _, _ -> 0 }
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedMode by rememberSaveable(stateSaver = GameModeSaver) {
        mutableStateOf<GameMode?>(null)
    }

    var selectedDifficulty by rememberSaveable(stateSaver = ModeDifficultySaver) {
        mutableStateOf<ModeDifficulty?>(null)
    }


    val modes = listOf(
        GameMode.BubbleMathBlitz(BubbleMathDifficulty.EASY),
        GameMode.QuizGenius(QuizDifficulty.BEGINNER)
    )

    if (selectedMode != null) {
        BackHandler {
            selectedMode = null
            selectedDifficulty = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF2A0A45),
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.primary,
                        Color(0xFF0A0420)
                    )
                )
            )
            .repeatLiquidBackground()
            .glowingOrbs()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .animatedBackground()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeonText(
                    text = "SELECT YOUR CHALLENGE",
                    gradient = listOf(Color(0xFF00F9FF), Color(0xFF8A2BE2)),
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 16.dp),
                    fontSize = 20.sp
                )
                Image(
                    painter = painterResource(id = R.drawable.math_mascot),
                    contentDescription = null,
                    modifier = Modifier
                        .statusBarsPadding()
                        .size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Step 1: GameMode selection - Always visible but fades/scales out when mode is selected
            AnimatedVisibility(
                visible = selectedMode == null,
                enter = fadeIn(animationSpec = tween(400, delayMillis = 200)) +
                        scaleIn(
                            initialScale = 0.8f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioLowBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ),
                exit = fadeOut(animationSpec = tween(300)) +
                        scaleOut(
                            targetScale = 0.8f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ),
                label = "GameModeSelection"
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MaterialSectionTitle(text = "Choose Game Mode")
                    Spacer(modifier = Modifier.height(16.dp))

                    modes.forEach { modeOption ->
                        Material3GameModeCard(
                            mode = modeOption,
                            isSelected = false, // Never show as selected in this view
                            onClick = {
                                selectedMode = modeOption
                                selectedDifficulty = null
                            }
                        )
                    }
                }
            }

            // Step 2: Difficulty selection - Slides up from bottom with smooth animation
            AnimatedVisibility(
                visible = selectedMode != null,
                enter = fadeIn(animationSpec = tween(400, delayMillis = 150)) +
                        slideInVertically(
                            initialOffsetY = { it / 2 }, // Start from halfway down
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ) +
                        expandVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ),
                exit = fadeOut(animationSpec = tween(250)) +
                        slideOutVertically(
                            targetOffsetY = { it / 3 },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ) +
                        shrinkVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        ),
                label = "DifficultySelection"
            ) {
                selectedMode?.let { targetMode ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MaterialSectionTitle(text = "Choose Difficulty for ${targetMode.name}")

                        val availableDifficulties = when (targetMode) {
                            is GameMode.BubbleMathBlitz -> BubbleMathDifficulty.entries
                            is GameMode.QuizGenius -> QuizDifficulty.entries
                        }

                        Material3LevelSelectionGrid(
                            levels = availableDifficulties,
                            selectedLevel = selectedDifficulty,
                            onLevelSelected = { level ->
                                selectedDifficulty = when (targetMode) {
                                    is GameMode.BubbleMathBlitz -> ModeDifficulty.Bubble(level as BubbleMathDifficulty)
                                    is GameMode.QuizGenius -> ModeDifficulty.Quiz(level as QuizDifficulty)
                                }
                            }
                        )

                        // High Score display with smooth entrance
                        AnimatedVisibility(
                            visible = selectedDifficulty != null,
                            enter = fadeIn(animationSpec = tween(300, delayMillis = 200)) +
                                    expandVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
                            exit = fadeOut(animationSpec = tween(200)) +
                                    shrinkVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy))
                        ) {
                            selectedDifficulty?.let { diff ->
                                val highScore = lastHighScoreProvider(targetMode, diff)
                                if (highScore > 0) {
                                    Column {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Surface(
                                            shape = RoundedCornerShape(16.dp),
                                            color = MaterialTheme.colorScheme.primaryContainer.copy(
                                                alpha = 0.3f
                                            ),
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    tint = Color(0xFFFFD700),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Text(
                                                    text = "Best Score: $highScore",
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    fontWeight = FontWeight.Bold,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Action buttons with staggered animation
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(300, delayMillis = 300)) +
                                    slideInVertically(
                                        initialOffsetY = { it / 4 },
                                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                                    )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Material3GradientButton(
                                    text = "Start ${targetMode.name}",
                                    enabled = selectedDifficulty != null,
                                    gradient = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    ),
                                    onClick = {
                                        coroutineScope.launch {
                                            if (selectedDifficulty != null) {
                                                onConfirm(targetMode, selectedDifficulty!!)
                                            }
                                        }
                                    }
                                )

                                /*
                                OutlinedButton(
                                    onClick = {
                                        selectedMode = null
                                        selectedDifficulty = null
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    ),
                                    border = ButtonDefaults.outlinedButtonBorder().copy(
                                        brush = Brush.linearGradient(
                                            listOf(
                                                MaterialTheme.colorScheme.outline,
                                                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                            )
                                        )
                                    )
                                ) {
                                    Text("Back to Mode Selection")
                                }
                                 */
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Material3GameModeCard(
    mode: GameMode,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "cardScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(120.dp)
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 12.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isSelected) {
                        Modifier.background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                                )
                            )
                        )
                    } else Modifier
                )
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else
                        MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(70.dp)
                ) {
                    Image(
                        painter = painterResource(
                            id = when (mode) {
                                is GameMode.BubbleMathBlitz -> R.drawable.bubblemuscot
                                is GameMode.QuizGenius -> R.drawable.math_mascot
                            }
                        ),
                        contentDescription = mode.name,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mode.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (mode) {
                            is GameMode.BubbleMathBlitz -> "Fast-paced arithmetic challenges!"
                            is GameMode.QuizGenius -> "Strategic quiz with diverse difficulties!"
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (isSelected) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Selected",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Material3LevelSelectionGrid(
    levels: List<Any>,
    selectedLevel: Any?,
    onLevelSelected: (Any) -> Unit
) {
    val haptics = LocalHapticFeedback.current


    val difficultyColors = listOf(
        MaterialTheme.colorScheme.primary,        // Easy - Primary color
        MaterialTheme.colorScheme.secondary,      // Medium - Secondary color
        MaterialTheme.colorScheme.tertiary,       // Hard - Tertiary color
        MaterialTheme.colorScheme.error           // Expert - Error color for danger
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        levels.forEachIndexed { index, level ->
            val isSelected = selectedLevel?.let { selected ->
                when (selected) {
                    is ModeDifficulty.Bubble -> selected.difficulty == level
                    is ModeDifficulty.Quiz -> selected.difficulty == level
                    else -> false
                }
            } ?: false

            val levelColor = difficultyColors.getOrElse(index) { MaterialTheme.colorScheme.primary }
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.1f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                label = "levelScale"
            )

            Surface(
                modifier = Modifier
                    .width(130.dp)
                    .height(70.dp)
                    .neumorphicShadow(
                        offset = 4.dp,
                        blurRadius = 12.dp,
                        shape = RoundedCornerShape(20.dp),
                        lightColor = MaterialTheme.colorScheme.primary,
                        darkColor = MaterialTheme.colorScheme.secondary,
                        inverted = isSelected
                    )
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLevelSelected(level)
                    },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) levelColor else MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = if (isSelected) 8.dp else 2.dp,
                shadowElevation = if (isSelected) 12.dp else 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (isSelected) {
                                Modifier.background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            levelColor.copy(alpha = 0.8f),
                                            levelColor
                                        )
                                    )
                                )
                            } else {
                                Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = when (level) {
                                is BubbleMathDifficulty -> level.name
                                is QuizDifficulty -> level.name
                                else -> "Unknown"
                            },
                            color = if (isSelected)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 0.5.sp,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelLarge
                        )

                        if (isSelected) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NeonText(
    text: String,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 24.sp
) {
    val brush = remember { Brush.linearGradient(colors = gradient) }

    Text(
        text = text,
        fontSize = fontSize,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        modifier = modifier,
        style = MaterialTheme.typography.headlineMedium.copy(
            brush = brush,
            shadow = Shadow(
                color = gradient.first().copy(alpha = 0.5f),
                offset = Offset(2f, 2f),
                blurRadius = 12f
            )
        )
    )
}

@Composable
fun Material3GradientButton(
    modifier: Modifier = Modifier,
    text: String,
    gradient: List<Color>,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.95f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "buttonScale"
    )

    Surface(
        onClick = {
            if (enabled) {
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                onClick()
            }
        },
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp)),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
        shadowElevation = if (enabled) 8.dp else 0.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = if (enabled)
                        Brush.linearGradient(gradient)
                    else
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                )
                .padding(horizontal = 32.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (enabled)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun MaterialSectionTitle(text: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
    }
}


private val GameMode.name: String
    get() = when (this) {
        is GameMode.BubbleMathBlitz -> "Bubble Blitz"
        is GameMode.QuizGenius -> "Quiz Genius"
    }

@PreviewScreenSizes
@Composable
private fun PreviewGameModes() {
    MultiplyTheme {
        GameModeSelectionScreen(onConfirm = { _, _ -> })
    }
}