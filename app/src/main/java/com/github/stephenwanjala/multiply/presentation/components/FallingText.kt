package com.github.stephenwanjala.multiply.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp

@Composable
fun FallingText(text: String, onFall: () -> Unit, onAnswer: () -> Unit) {
    // Get the screen height
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    // Animate the vertical offset from 0 to screen height
    val offset by animateDpAsState(
        targetValue = screenHeight,
        animationSpec = tween(durationMillis = 5000, easing = LinearEasing), label = ""
    )
    // Launch a coroutine when the text reaches the bottom or the user answers correctly
    LaunchedEffect(offset) {
        if (offset == screenHeight) {
            onFall()
        } else if (text.toInt() == 10) { // You need to define the answer variable
            onAnswer()
        }
    }
    // Display the text with the animated offset
    Text(
        text = text,
        modifier = Modifier.offset(y = offset)
    )
}
