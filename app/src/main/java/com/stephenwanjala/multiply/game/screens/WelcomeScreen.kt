package com.stephenwanjala.multiply.game.screens

import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.EaseOutBounce
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stephenwanjala.multiply.R
import com.stephenwanjala.multiply.game.components.AnimatedBackground
import com.stephenwanjala.multiply.ui.theme.MultiplyTheme

@Composable
fun WelcomeScreen(
    onPlayClick: () -> Unit,
    onHowToPlayClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF4CAF50),
                        MaterialTheme.colorScheme.background,
                        Color(0xFF81D4FA)  
                    ),
                )
            )
    )  {

        AnimatedBackground()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Animated logo
            AnimatedLogo()

            // Mascot
            Image(
                painter = painterResource(id = R.drawable.math_mascot),
                contentDescription = "Math Mascot",
                modifier = Modifier.size(200.dp)
            )

            // Buttons
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                PulsatingButton(
                    onClick = onPlayClick,
                    text = "Play!",
                    color = Color(0xFF4CAF50),
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onHowToPlayClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("How to Play", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onSettingsClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C4DFF)),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Settings", fontSize = 18.sp)
                }
            }
        }
    }
}


@Composable
fun AnimatedLogo() {
    var logoScale by remember { mutableFloatStateOf(0.5f) }

    LaunchedEffect(Unit) {
        animate(
            initialValue = 0.5f,
            targetValue = 1f,
            animationSpec = tween(1000, easing = EaseOutBounce)
        ) { value, _ -> logoScale = value }
    }

    Text(
        text = "Math\n\nChallenge!",
        fontSize = 48.sp,
        fontWeight = FontWeight.Bold,
//        color = Color.White,
        modifier = Modifier
            .graphicsLayer {
                scaleX = logoScale
                scaleY = logoScale
            }
            .padding(top = 48.dp)
    )
}

@Composable
fun PulsatingButton(onClick: () -> Unit, text: String, color: Color) {
    var buttonScale by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(Unit) {
        while (true) {
            animate(
                initialValue = 1f,
                targetValue = 1.2f,
                animationSpec = tween(500, easing = EaseInOutQuad)
            ) { value, _ -> buttonScale = value }
            animate(
                initialValue = 1.2f,
                targetValue = 1f,
                animationSpec = tween(500, easing = EaseInOutQuad)
            ) { value, _ -> buttonScale = value }
        }
    }

    Button(
        onClick = onClick,
        modifier = Modifier
            .graphicsLayer {
                scaleX = buttonScale
                scaleY = buttonScale
            }
            .size(width = 200.dp, height = 60.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}


@PreviewLightDark
@Composable
private fun PreviewWelcomeScreen() {
    MultiplyTheme {
        WelcomeScreen(onSettingsClick = {}, onPlayClick = {}, onHowToPlayClick = {})
    }
}
