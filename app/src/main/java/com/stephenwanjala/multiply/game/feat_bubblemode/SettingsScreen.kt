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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowWidthSizeClass
import com.stephenwanjala.multiply.ui.theme.MultiplyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBackClick: () -> Unit, state: GameState, onAction: (GameAction) -> Unit) {
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
                            Color(0xFFE1F5FE),
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
                        DifficultySelector(uiDiff) { onAction(GameAction.UpdateDifficulty(it.toDifficulty())) }
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
                            DifficultySelector(uiDiff) { onAction(GameAction.UpdateDifficulty(it.toDifficulty())) }
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
                Button(onClick = { /* Reset settings */ }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000))) {
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
            Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF3F51B5), modifier = Modifier.padding(bottom = 16.dp))
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
        Switch(checked = isEnabled, onCheckedChange = onToggle, colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF4CAF50), checkedTrackColor = Color(0xFF81C784)))
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
        "Space" -> Color(0xFF3F51B5)
        "Jungle" -> Color(0xFF4CAF50)
        "Ocean" -> Color(0xFF03A9F4)
        "Candy" -> Color(0xFFE91E63)
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

fun Difficulty.toInt() = when (this) {
    Difficulty.EASY -> 1
    Difficulty.MEDIUM -> 2
    Difficulty.HARD -> 3
}

fun Int.toDifficulty() = when (this) {
    1 -> Difficulty.EASY
    2 -> Difficulty.MEDIUM
    else -> Difficulty.HARD
}

@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun SettingsPreview() {
    MultiplyTheme {
        SettingsScreen(onBackClick = {}, state = GameState()) { }
    }
}
