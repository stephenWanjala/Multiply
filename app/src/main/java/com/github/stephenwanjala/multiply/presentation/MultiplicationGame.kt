package com.github.stephenwanjala.multiply.presentation

import android.graphics.Insets
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination(start = true)
fun MultiplicationGame() {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(text = "Score: score")
                            Text(text = "Lives: lives")
                        }
                    }
                )
            },

            ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val text = remember { mutableStateOf("Hello") }
               TetrisText(text ="Hello" ) {
                   println("Helloooooo")
               }

            }
        }

    }

}

@Composable
fun TetrisText(text: String, onFinished: () -> Unit) {
    // Create a transition that animates the offset and alpha of the text
    val transition = rememberInfiniteTransition(label = "transition")
    val offset by transition.animateFloat(
        initialValue = -200f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,

        ),
        label = "alpha"
    )

    // Use AnimatedVisibility to show or hide the text based on the alpha value
    AnimatedVisibility(
        visible = alpha > 0f,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        // Use Modifier.offset to move the text along the y-axis
        Text(
            text = text,
            modifier = Modifier.offset(y = offset.dp)
        )
    }

    // Call the onFinished callback when the text is invisible
    if (alpha == 0f) {
        onFinished()
    }
}
