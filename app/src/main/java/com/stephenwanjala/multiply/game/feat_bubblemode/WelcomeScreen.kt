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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stephenwanjala.multiply.R
import com.stephenwanjala.multiply.core.designsystem.component.AnimatedFloatingSymbolsBackground
import com.stephenwanjala.multiply.ui.theme.MultiplyTheme


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
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
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
                    AnimatedGameTitle()

                    AnimatedMascot(
                        modifier = Modifier
                            .size(200.dp)
                            .padding(top = 16.dp, bottom = 24.dp)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                    ) {
                        PulsatingGameButton(
                            onClick = onPlayClick,
                            text = "Play!",
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        GameButton(
                            onClick = onHowToPlayClick,
                            text = "How to Play",
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(20.dp))
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
        alphaAnim.animateTo(1f, animationSpec = tween(500, easing = EaseInOutQuad))
        animate(
            initialValue = 0.5f,
            targetValue = 1.05f,
            animationSpec = tween(800, easing = EaseOutBounce)
        ) { value, _ -> logoScale = value }
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
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        lineHeight = 50.sp,
        style = MaterialTheme.typography.displayLarge,
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
        scaleAnim.animateTo(
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500, easing = EaseInOutQuad),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    LaunchedEffect(Unit) {
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
            targetValue = 1.1f,
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
            ),
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
            style = MaterialTheme.typography.headlineMedium
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
            style = MaterialTheme.typography.titleLarge
        )
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
