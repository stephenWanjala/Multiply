package com.stephenwanjala.multiply.game.feat_bubblemode

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.stephenwanjala.multiply.ui.theme.LocalMultiplyColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowWidthSizeClass
import com.stephenwanjala.multiply.game.models.BubbleMathDifficulty
import com.stephenwanjala.multiply.ui.theme.MultiplyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBackClick: () -> Unit, state: GameState, onEvent: (BubbleGameEvent) -> Unit) {
    var soundEnabled by remember { mutableStateOf(true) }
    var musicEnabled by remember { mutableStateOf(true) }
    var selectedTheme by remember { mutableStateOf("Space") }

    val uiDiff = state.selectedDifficulty.toInt()
    val scrollState = rememberScrollState()
    val adaptiveInfo = currentWindowAdaptiveInfo()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = { Text("Game Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
                    }
                })
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
                    SettingsCategory("Gameplay") {
                        DifficultySelector(uiDiff) {
                            onEvent(BubbleGameEvent.UpdateDifficulty(it.toBubbleDifficulty()))
                        }
                    }
                    SettingsCategory("Audio") {
                        SoundToggle("Sound Effects", soundEnabled) { soundEnabled = it }
                        SoundToggle("Background Music", musicEnabled) { musicEnabled = it }
                    }
                    SettingsCategory("Appearance") {
                        ThemeSelector(selectedTheme) { selectedTheme = it }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        SettingsCategory("Gameplay", Modifier.weight(1f)) {
                            DifficultySelector(uiDiff) {
                                onEvent(BubbleGameEvent.UpdateDifficulty(it.toBubbleDifficulty()))
                            }
                        }
                        SettingsCategory("Audio", Modifier.weight(1f)) {
                            SoundToggle("Sound Effects", soundEnabled) { soundEnabled = it }
                            SoundToggle("Background Music", musicEnabled) { musicEnabled = it }
                        }
                    }
                    SettingsCategory("Appearance", Modifier.fillMaxWidth()) {
                        ThemeSelector(selectedTheme) { selectedTheme = it }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { onEvent(BubbleGameEvent.ResetSettings) },
                    colors = ButtonDefaults.buttonColors(containerColor = LocalMultiplyColors.current.warning)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset to Default", fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun SettingsCategory(title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(modifier = modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 16.dp))
            content()
        }
    }
}

@Composable
fun DifficultySelector(difficulty: Int, onDifficultyChange: (Int) -> Unit) {
    Column {
        Text("Difficulty Level")
        Slider(value = difficulty.toFloat(), onValueChange = { onDifficultyChange(it.toInt()) }, valueRange = 1f..3f, steps = 1, modifier = Modifier.padding(vertical = 8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Easy")
            Text("Medium")
            Text("Hard")
        }
    }
}

@Composable
fun SoundToggle(title: String, isEnabled: Boolean, onToggle: (Boolean) -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, modifier = Modifier.weight(1f))
        Switch(checked = isEnabled, onCheckedChange = onToggle, colors = SwitchDefaults.colors(checkedThumbColor = LocalMultiplyColors.current.success, checkedTrackColor = LocalMultiplyColors.current.successContainer))
    }
}

@Composable
fun ThemeSelector(selectedTheme: String, onThemeSelect: (String) -> Unit) {
    val themes = listOf("Space", "Jungle", "Ocean", "Candy")
    Column {
        Text("Game Theme", modifier = Modifier.padding(bottom = 8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            themes.forEach { theme ->
                ThemeButton(theme, theme == selectedTheme) { onThemeSelect(theme) }
            }
        }
    }
}

@Composable
fun ThemeButton(theme: String, isSelected: Boolean, onSelect: () -> Unit) {
    val color = when (theme) {
        "Space" -> MaterialTheme.colorScheme.primary
        "Jungle" -> LocalMultiplyColors.current.success
        "Ocean" -> MaterialTheme.colorScheme.secondary
        "Candy" -> MaterialTheme.colorScheme.tertiary
        else -> Color.Gray
    }
    val scale by animateFloatAsState(if (isSelected) 1.1f else 1f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    Box(modifier = Modifier
        .size(60.dp)
        .clip(MaterialTheme.shapes.medium)
        .background(if (isSelected) color else color.copy(alpha = 0.6f))
        .animateContentSize()
        .scale(scale)) {
        IconButton(onClick = onSelect) {
            Icon(Icons.Default.Palette, contentDescription = theme, tint = Color.White)
        }
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
