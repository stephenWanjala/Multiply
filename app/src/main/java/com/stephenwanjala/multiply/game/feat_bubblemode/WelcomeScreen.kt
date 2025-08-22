package com.stephenwanjala.multiply.game.feat_bubblemode

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stephenwanjala.multiply.R
import com.stephenwanjala.multiply.ui.theme.MultiplyTheme
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onPlayClick: () -> Unit,
    onHowToPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNavigateUp: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Math Master",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Go Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                ),
                windowInsets = WindowInsets.statusBars
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            // Use a theme-consistent background brush or color
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedFloatingSymbolsBackground()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated Game Title
                    AnimatedGameTitle()

                    // Mascot with a playful touch
                    AnimatedMascot(
                        modifier = Modifier
                            .size(200.dp)
                            .padding(top = 16.dp, bottom = 24.dp)
                    )

                    // Buttons
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                    ) {
                        // Play button (pulsating)
                        PulsatingGameButton(
                            onClick = onPlayClick,
                            text = "Play!",
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        // How to Play button
                        GameButton(
                            onClick = onHowToPlayClick,
                            text = "How to Play",
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        // Settings button
                        GameButton(
                            onClick = onSettingsClick,
                            text = "Settings",
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedGameTitle() {
    var logoScale by remember { mutableFloatStateOf(0.5f) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Animate scale
        alphaAnim.animateTo(1f, animationSpec = tween(500, easing = EaseInOutQuad))
        animate(
            initialValue = 0.5f,
            targetValue = 1.05f,
            animationSpec = tween(800, easing = EaseOutBounce)
        ) { value, _ -> logoScale = value }
        // Then a subtle infinite pulse
        while (true) {
            animate(
                initialValue = 1.05f,
                targetValue = 1.0f,
                animationSpec = tween(700, easing = EaseInOutQuad)
            ) { value, _ -> logoScale = value }
            animate(
                initialValue = 1.0f,
                targetValue = 1.05f,
                animationSpec = tween(700, easing = EaseInOutQuad)
            ) { value, _ -> logoScale = value }
        }
    }

    Text(
        text = "Math\nMaster!",
        fontSize = 56.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = BubbleFont,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        lineHeight = 50.sp,
        modifier = Modifier
            .graphicsLayer {
                scaleX = logoScale
                scaleY = logoScale
                alpha = alphaAnim.value
            }
            .padding(top = 32.dp, bottom = 16.dp)
            .widthIn(min = 250.dp)
    )
}

@Composable
fun AnimatedMascot(modifier: Modifier = Modifier) {
    val scaleAnim = remember { Animatable(1f) }
    val rotationAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Breathing animation for scale
        scaleAnim.animateTo(
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500, easing = EaseInOutQuad),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
        // Gentle float/rotation
        rotationAnim.animateTo(
            targetValue = 5f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = EaseInOutQuad),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Image(
        painter = painterResource(id = R.drawable.math_mascot),
        contentDescription = "Math Mascot",
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            .padding(12.dp)
            .graphicsLayer {
                scaleX = scaleAnim.value
                scaleY = scaleAnim.value
                rotationZ = rotationAnim.value
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin.Center
            }
    )
}


@Composable
fun PulsatingGameButton(
    onClick: () -> Unit,
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    val buttonScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        buttonScale.animateTo(
            targetValue = 1.1f, // Initial pulse scale
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 600, easing = EaseInOutQuad),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(64.dp)
            .graphicsLayer {
                scaleX = buttonScale.value
                scaleY = buttonScale.value
            }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(percent = 50),
                spotColor = containerColor
            ), // Shadow for depth
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(percent = 50),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp, pressedElevation = 2.dp)
    ) {
        Text(
            text,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = BubbleFont
        )
    }
}

@Composable
fun GameButton(
    onClick: () -> Unit,
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(56.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(percent = 50),
                spotColor = containerColor.copy(alpha = 0.5f)
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(percent = 50),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 1.dp)
    ) {
        Text(
            text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = BubbleFont
        )
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
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = 0.6f }) {
        repeat(15) {
            val symbol = remember { symbols.random() }
            val color = remember { colors.random() }
            val initialOffset = remember {
                androidx.compose.ui.geometry.Offset(
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
                                easing = androidx.compose.animation.core.LinearEasing
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
                                easing = androidx.compose.animation.core.LinearEasing
                            ),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                }

                // Animate Scale (pulsating)
                scope.launch {
                    animatableScale.animateTo(
                        targetValue = animatableScale.value * 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = Random.nextInt(1500, 2500),
                                easing = androidx.compose.animation.core.LinearEasing
                            ),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                }

                // Animate Rotation
                scope.launch {
                    animatableRotation.animateTo(
                        targetValue = animatableRotation.value + 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = Random.nextInt(10000, 20000),
                                easing = androidx.compose.animation.core.LinearEasing
                            ),
                            repeatMode = RepeatMode.Restart
                        )
                    )
                }

                // Animate Alpha (fading in/out slightly)
                scope.launch {
                    animatableAlpha.animateTo(
                        targetValue = animatableAlpha.value * 0.8f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = Random.nextInt(2000, 4000),
                                easing = androidx.compose.animation.core.LinearEasing
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
@PreviewScreenSizes
@Composable
private fun PreviewWelcomeScreen() {
    MultiplyTheme {
        WelcomeScreen(
            onSettingsClick = {},
            onPlayClick = {},
            onHowToPlayClick = {},
            onNavigateUp = {}
        )
    }
}