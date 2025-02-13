package com.stephenwanjala.multiply.game.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stephenwanjala.multiply.game.screens.gamescreen.Difficulty
import com.stephenwanjala.multiply.game.screens.gamescreen.GameAction
import com.stephenwanjala.multiply.game.screens.gamescreen.GameState
import com.stephenwanjala.multiply.ui.theme.MultiplyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBackClick: () -> Unit, state: GameState, onAction: (GameAction)->Unit) {
    var difficulty by remember { mutableIntStateOf(1) }
    var soundEnabled by remember { mutableStateOf(true) }
    var musicEnabled by remember { mutableStateOf(true) }
    var selectedTheme by remember { mutableStateOf("Space") }
    val uiDiff = when(state.selectedDifficulty){
        Difficulty.EASY -> 1
        Difficulty.MEDIUM -> 2
        Difficulty.HARD -> 3
    }

    val scrollState = rememberScrollState()
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Game Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }, navigationIcon = {
                IconButton(onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            })
        }) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
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
//            .background(Color(0xFFE1F5FE))
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {

                SettingsCategory(title = "Gameplay") {
                    DifficultySelector(
                        difficulty = uiDiff,
                        onDifficultyChange = { difficulty = it
                            onAction(GameAction.UpdateDifficulty(it.toDifficulty()))
                        }
                    )
                }

                SettingsCategory(title = "Audio") {
                    SoundToggle(
                        isEnabled = soundEnabled,
                        onToggle = { soundEnabled = it },
                        title = "Sound Effects"
                    )
                    SoundToggle(
                        isEnabled = musicEnabled,
                        onToggle = { musicEnabled = it },
                        title = "Background Music"
                    )
                }

                SettingsCategory(title = "Appearance") {
                    ThemeSelector(
                        selectedTheme = selectedTheme,
                        onThemeSelect = { selectedTheme = it }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { /* Reset settings to default */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)),
                    shape = MaterialTheme.shapes.medium
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
fun SettingsCategory(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3F51B5),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
        }
    }
}

@Composable
fun DifficultySelector(difficulty: Int, onDifficultyChange: (Int) -> Unit) {
    Text("Difficulty Level", style = MaterialTheme.typography.bodyLarge)
    Slider(
        value = difficulty.toFloat(),
        onValueChange = { onDifficultyChange(it.toInt()) },
        valueRange = 1f..3f,
        steps = 1,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Easy")
        Text("Medium")
        Text("Hard")
    }
}

@Composable
fun SoundToggle(isEnabled: Boolean, onToggle: (Boolean) -> Unit, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF4CAF50),
                checkedTrackColor = Color(0xFF81C784)
            )
        )
    }
}

@Composable
fun ThemeSelector(selectedTheme: String, onThemeSelect: (String) -> Unit) {
    val themes = listOf("Space", "Jungle", "Ocean", "Candy")
    Column {
        Text(
            "Game Theme",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            themes.forEach { theme ->
                ThemeButton(
                    theme = theme,
                    isSelected = theme == selectedTheme,
                    onSelect = { onThemeSelect(theme) }
                )
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

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(if (isSelected) color else color.copy(alpha = 0.6f))
            .animateContentSize()
            .scale(scale)
    ) {
        IconButton(onClick = onSelect, modifier = Modifier.fillMaxSize()) {
            /*
             Icon(
                imageVector = when (theme) {
                    "Space" -> Icons.Default.Star
                    "Jungle" -> Icons.Default.Park
                    "Ocean" -> Icons.Default.Water
                    "Candy" -> Icons.Default.Cake
                    else -> Icons.Default.Palette
                },
                contentDescription = theme,
                tint = Color.White
            )
             */
            Icon(
                imageVector = when (theme) {
                    "Space" -> Icons.Default.Star
                    "Jungle" -> Icons.Default.Face
                    "Ocean" -> Icons.Default.Warning
                    "Candy" -> Icons.Default.Info
                    else -> Icons.Default.PlayArrow
                },
                contentDescription = theme,
                tint = Color.White
            )
        }
    }
}

fun Int.toDifficulty():Difficulty{
    return when {
        this==1 -> {
            Difficulty.EASY
        }
        this==2 -> {
            Difficulty.MEDIUM
        }
        else -> {
            Difficulty.HARD
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewSettings() {
    MultiplyTheme {
        SettingsScreen(onBackClick = {}, state = GameState(), onAction ={})
    }
}