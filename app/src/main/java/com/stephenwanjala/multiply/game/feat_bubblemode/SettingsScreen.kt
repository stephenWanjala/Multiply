package com.stephenwanjala.multiply.game.feat_bubblemode

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowWidthSizeClass
import com.stephenwanjala.multiply.core.designsystem.component.AnimatedFloatingSymbolsBackground
import com.stephenwanjala.multiply.game.models.BubbleMathDifficulty
import com.stephenwanjala.multiply.ui.theme.LocalMultiplyColors
import com.stephenwanjala.multiply.ui.theme.MultiplyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    state: GameState,
    onEvent: (BubbleGameEvent) -> Unit
) {
    var soundEnabled by remember { mutableStateOf(true) }
    var musicEnabled by remember { mutableStateOf(true) }
    var selectedTheme by remember { mutableStateOf("Space") }

    val scrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isCompact =
        adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Navigate Up",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.statusBars
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedFloatingSymbolsBackground(alpha = 0.3f)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    SettingsSection(
                        title = "Difficulty",
                        subtitle = "Pick how spicy your math should be"
                    ) {
                        DifficultyPicker(
                            selected = state.selectedDifficulty,
                            onSelect = { onEvent(BubbleGameEvent.UpdateDifficulty(it)) }
                        )
                    }

                    if (isCompact) {
                        SettingsSection(
                            title = "Audio",
                            subtitle = "Mute the bubbles or crank the beats"
                        ) {
                            AudioToggleRow(
                                icon = Icons.AutoMirrored.Filled.VolumeUp,
                                accent = LocalMultiplyColors.current.success,
                                title = "Sound Effects",
                                description = "Pops, dings and celebrations",
                                enabled = soundEnabled,
                                onToggle = { soundEnabled = it }
                            )
                            Spacer(Modifier.height(10.dp))
                            AudioToggleRow(
                                icon = Icons.Default.MusicNote,
                                accent = MaterialTheme.colorScheme.tertiary,
                                title = "Background Music",
                                description = "Chill beats while you play",
                                enabled = musicEnabled,
                                onToggle = { musicEnabled = it }
                            )
                        }
                        SettingsSection(
                            title = "Theme",
                            subtitle = "Choose your vibe"
                        ) {
                            ThemePicker(selected = selectedTheme, onSelect = { selectedTheme = it })
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                SettingsSection(
                                    title = "Audio",
                                    subtitle = "Mute the bubbles or crank the beats"
                                ) {
                                    AudioToggleRow(
                                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                                        accent = LocalMultiplyColors.current.success,
                                        title = "Sound Effects",
                                        description = "Pops and celebrations",
                                        enabled = soundEnabled,
                                        onToggle = { soundEnabled = it }
                                    )
                                    Spacer(Modifier.height(10.dp))
                                    AudioToggleRow(
                                        icon = Icons.Default.MusicNote,
                                        accent = MaterialTheme.colorScheme.tertiary,
                                        title = "Background Music",
                                        description = "Chill beats while you play",
                                        enabled = musicEnabled,
                                        onToggle = { musicEnabled = it }
                                    )
                                }
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                SettingsSection(
                                    title = "Theme",
                                    subtitle = "Choose your vibe"
                                ) {
                                    ThemePicker(
                                        selected = selectedTheme,
                                        onSelect = { selectedTheme = it }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    DuoButton(
                        text = "Reset to Default",
                        onClick = { onEvent(BubbleGameEvent.ResetSettings) },
                        containerColor = LocalMultiplyColors.current.warning,
                        contentColor = Color.White,
                        leading = Icons.Default.Refresh,
                        fontSize = 18,
                        height = 60.dp,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    subtitle: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(18.dp)
    ) {
        Text(
            text = title.uppercase(),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.4.sp
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = subtitle,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(14.dp))
        content()
    }
}

@Composable
private fun DifficultyPicker(
    selected: BubbleMathDifficulty,
    onSelect: (BubbleMathDifficulty) -> Unit
) {
    val options = listOf(
        DifficultyOption(
            difficulty = BubbleMathDifficulty.EASY,
            emoji = "🌱",
            label = "Easy",
            tagline = "Slow & friendly",
            color = LocalMultiplyColors.current.success
        ),
        DifficultyOption(
            difficulty = BubbleMathDifficulty.MEDIUM,
            emoji = "🔥",
            label = "Medium",
            tagline = "Quick thinking",
            color = LocalMultiplyColors.current.warning
        ),
        DifficultyOption(
            difficulty = BubbleMathDifficulty.HARD,
            emoji = "⚡",
            label = "Hard",
            tagline = "Lightning round",
            color = MaterialTheme.colorScheme.error
        )
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEach { option ->
            DifficultyTile(
                option = option,
                isSelected = selected == option.difficulty,
                onClick = { onSelect(option.difficulty) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private data class DifficultyOption(
    val difficulty: BubbleMathDifficulty,
    val emoji: String,
    val label: String,
    val tagline: String,
    val color: Color
)

@Composable
private fun DifficultyTile(
    option: DifficultyOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.04f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "diffScale"
    )
    val face = if (isSelected) option.color else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

    Column(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(18.dp))
            .background(face)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = option.emoji,
            fontSize = 32.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = option.label,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = option.tagline,
            color = textColor.copy(alpha = 0.8f),
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun AudioToggleRow(
    icon: ImageVector,
    accent: Color,
    title: String,
    description: String,
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = description,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                fontSize = 12.sp
            )
        }
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = LocalMultiplyColors.current.success,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun ThemePicker(selected: String, onSelect: (String) -> Unit) {
    val themes = listOf(
        ThemeOption("Space", "🚀", MaterialTheme.colorScheme.primary),
        ThemeOption("Jungle", "🌴", LocalMultiplyColors.current.success),
        ThemeOption("Ocean", "🌊", MaterialTheme.colorScheme.secondary),
        ThemeOption("Candy", "🍭", MaterialTheme.colorScheme.tertiary)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        themes.forEach { theme ->
            ThemeTile(
                option = theme,
                isSelected = selected == theme.name,
                onClick = { onSelect(theme.name) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private data class ThemeOption(val name: String, val emoji: String, val color: Color)

@Composable
private fun ThemeTile(
    option: ThemeOption,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.06f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "themeScale"
    )
    Column(
        modifier = modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) option.color else option.color.copy(alpha = 0.18f)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) Color.White.copy(alpha = 0.22f)
                    else option.color.copy(alpha = 0.25f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = option.emoji, fontSize = 22.sp)
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = option.name,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.Black
        )
    }
}

fun BubbleMathDifficulty.toInt() = when (this) {
    BubbleMathDifficulty.EASY -> 1
    BubbleMathDifficulty.MEDIUM -> 2
    BubbleMathDifficulty.HARD -> 3
}

fun Int.toBubbleDifficulty() = when (this) {
    1 -> BubbleMathDifficulty.EASY
    2 -> BubbleMathDifficulty.MEDIUM
    else -> BubbleMathDifficulty.HARD
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun SettingsPreview() {
    MultiplyTheme {
        SettingsScreen(onBackClick = {}, state = GameState()) { }
    }
}
