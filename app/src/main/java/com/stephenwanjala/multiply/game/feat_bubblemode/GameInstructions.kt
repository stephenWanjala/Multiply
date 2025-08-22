package com.stephenwanjala.multiply.game.feat_bubblemode

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stephenwanjala.multiply.R
import com.stephenwanjala.multiply.game.utlis.randomOffset
import com.stephenwanjala.multiply.ui.theme.MultiplyTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructionsScreen(onStartGame: () -> Unit, navigateUp: () -> Unit) {
    val scrollState = rememberScrollState()
    var startButtonScale by remember { mutableFloatStateOf(1f) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(
                text = "Game Instructions",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )
        }, navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Navigate Up"
                )
            }
        }, scrollBehavior = scrollBehavior)
    }) { paddingValues ->

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Animated background
                AnimatedBackground()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Mascot
                    Image(
                        painter = painterResource(id = R.drawable.math_mascot),
                        contentDescription = "Math Mascot",
                        modifier = Modifier
                            .size(150.dp)
                            .padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Math Adventure!",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                    ) {
                        InstructionCard(
                            title = "How to Play",
                            items = listOf(
                                "Solve multiplication problems",
                                "Answer before time runs out",
                                "Earn points for correct answers",
                                "Lose lives for mistakes",
                                "Beat your high score!"
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        InstructionCard(
                            title = "Cool Features",
                            items = listOf(
                                "Fun math challenges",
                                "Colorful graphics",
                                "Exciting sound effects",
                                "Track your progress",
                                "Compete with friends"
                            )
                        )
//            Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = onStartGame,
                            modifier = Modifier
                                .scale(startButtonScale)
                                .animateContentSize(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("Start Your Adventure!", fontSize = 18.sp)
                        }
                    }






                    LaunchedEffect(Unit) {
                        while (true) {
                            startButtonScale = 1.1f
                            delay(500)
                            startButtonScale = 1f
                            delay(500)
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun InstructionCard(title: String, items: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3F51B5),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            items.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        Color(
                                            0xFFFFA000
                                        ),
                                        MaterialTheme.colorScheme.primary
                                    )
                                ), shape = RoundedCornerShape(50)
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item, style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedBackground() {
    val symbols = listOf("+", "-", "×", "÷", "=")
    val colors = listOf(Color.Red, Color.Blue, Color.Green, Color.Magenta, Color.Cyan)

    Box(modifier = Modifier.fillMaxSize()) {
        repeat(20) {
            var position by remember { mutableStateOf(randomOffset()) }
            val symbol = remember { symbols.random() }
            val color = remember { colors.random() }

            Text(
                text = symbol,
                color = color,
                fontSize = 24.sp,
                modifier = Modifier
                    .offset { IntOffset(position.x.toInt(), position.y.toInt()) }
            )

            LaunchedEffect(Unit) {
                while (true) {
                    delay(Random.nextLong(3000, 5000))
                    position = randomOffset()
                }
            }
        }
    }
}


@PreviewLightDark
@Composable
private fun PreviewInstructionsScreen() {
    MultiplyTheme {
        InstructionsScreen(onStartGame = {}, navigateUp = {})

    }

}

