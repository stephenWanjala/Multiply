package com.stephenwanjala.multiply.game.feat_bubblemode

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stephenwanjala.multiply.R
import com.stephenwanjala.multiply.ui.theme.MultiplyTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random


val BubbleFont = FontFamily.Monospace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructionsScreen(onStartGame: () -> Unit, navigateUp: () -> Unit) {
    val scrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Game Instructions",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        fontFamily = BubbleFont,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
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
                // Animated background
                AnimatedFloatingSymbolsBackground()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.math_mascot),
                        contentDescription = "Math Mascot",
                        modifier = Modifier
                            .size(180.dp)
                            .padding(bottom = 16.dp)
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                            )
                            .padding(8.dp)
                            .graphicsLayer {
                                // Add a subtle breathing animation
                                val scaleAnimatable = Animatable(1f)
                                scope.launch {
                                    scaleAnimatable.animateTo(
                                        targetValue = 1.05f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(
                                                durationMillis = 1500,
                                                easing = LinearEasing
                                            ),
                                            repeatMode = RepeatMode.Reverse
                                        )
                                    )
                                }
                                scaleX = scaleAnimatable.value
                                scaleY = scaleAnimatable.value
                            }
                    )

                    Text(
                        text = "Math Adventure!",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = BubbleFont,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 24.dp),
                        textAlign = TextAlign.Center
                    )

                    InstructionCard(
                        title = "How to Play",
                        items = listOf(
                            "Solve multiplication problems swiftly.",
                            "Answer correctly before time runs out.",
                            "Earn shiny points for every right answer.",
                            "Watch out! Mistakes cost you lives.",
                            "Challenge yourself and beat your high score!"
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    InstructionCard(
                        title = "Cool Features",
                        items = listOf(
                            "Engaging math challenges for all ages.",
                            "Vibrant, eye-catching graphics.",
                            "Exciting sound effects to keep you going.",
                            "Track your progress and see yourself grow.",
                            "Compete with friends for top scores!"
                        ),
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    StartGameButton(onStartGame = onStartGame)
                }
            }
        }
    }
}

@Composable
private fun StartGameButton(onStartGame: () -> Unit) {
    var startButtonScale by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(Unit) {
        while (true) {
            startButtonScale = 1.08f
            delay(400)
            startButtonScale = 1f
            delay(400)
        }
    }

    Button(
        onClick = onStartGame,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(56.dp)
            .graphicsLayer {
                scaleX = startButtonScale
                scaleY = startButtonScale
            }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(percent = 50),
                spotColor = MaterialTheme.colorScheme.primary
            ),
        shape = RoundedCornerShape(percent = 50),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp)
    ) {
        Text(
            text = "Start Your Adventure!",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = BubbleFont
        )
    }
}


@Composable
fun InstructionCard(title: String, items: List<String>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp) // Use theme surface with elevation
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                fontFamily = BubbleFont,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            items.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary,
                                    )
                                ),
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedFloatingSymbolsBackground() {
    val symbols = listOf("➕", "➖", "✖️", "➗", "⚡", "⭐", "✅", "🔢")
    val colors = listOf(
        MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
    )

    val screenWidth = LocalWindowInfo.current.containerSize.width.toFloat()
    val screenHeight = LocalWindowInfo.current.containerSize.height.toFloat()
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(0.6f)
    ) {
        repeat(15) {
            val symbol = remember { symbols.random() }
            val color = remember { colors.random() }
            val initialOffset = remember {
                Offset(
                    x = Random.nextFloat() * screenWidth,
                    y = Random.nextFloat() * screenHeight
                )
            }
            val animatableOffsetX = remember { Animatable(initialOffset.x) }
            val animatableOffsetY = remember { Animatable(initialOffset.y) }
            val animatableScale =
                remember { Animatable(Random.nextFloat() * 0.5f + 0.5f) }
            val animatableRotation = remember { Animatable(Random.nextFloat() * 360f) }
            val animatableAlpha =
                remember { Animatable(0.3f + Random.nextFloat() * 0.4f) }

            LaunchedEffect(Unit) {
                // Animate X movement (left to right or right to left)
                scope.launch {
                    val targetX = if (Random.nextBoolean()) -50f else screenWidth + 50f
                    animatableOffsetX.animateTo(
                        targetValue = targetX,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = Random.nextInt(8000, 15000),
                                easing = LinearEasing
                            ),
                            repeatMode = RepeatMode.Restart
                        )
                    )
                }

                // Animate Y movement (subtle up and down or random)
                scope.launch {
                    val targetY =
                        initialOffset.y + Random.nextFloat() * 100 - 50
                    animatableOffsetY.animateTo(
                        targetValue = targetY,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = Random.nextInt(7000, 12000),
                                easing = LinearEasing
                            ),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                }

                // Animate Scale (pulsating)
                launch {
                    animatableScale.animateTo(
                        targetValue = animatableScale.value * 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = Random.nextInt(1500, 2500),
                                easing = LinearEasing
                            ),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                }

                // Animate Rotation
                launch {
                    animatableRotation.animateTo(
                        targetValue = animatableRotation.value + 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = Random.nextInt(10000, 20000),
                                easing = LinearEasing
                            ),
                            repeatMode = RepeatMode.Restart
                        )
                    )
                }

                // Animate Alpha (fading in/out slightly)
                launch {
                    animatableAlpha.animateTo(
                        targetValue = animatableAlpha.value * 0.8f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = Random.nextInt(2000, 4000),
                                easing = LinearEasing
                            ),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                }
            }

            Text(
                text = symbol,
                color = color,
                fontSize = (28.sp * animatableScale.value),
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = animatableOffsetX.value.roundToInt(),
                            y = animatableOffsetY.value.roundToInt()
                        )
                    }
                    .graphicsLayer {
                        alpha = animatableAlpha.value
                        rotationZ = animatableRotation.value
                        scaleX = animatableScale.value
                        scaleY = animatableScale.value
                    }
            )
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