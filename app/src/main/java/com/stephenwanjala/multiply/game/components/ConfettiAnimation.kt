package com.stephenwanjala.multiply.game.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.stephenwanjala.multiply.game.utlis.Particle
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun ConfettiAnimation(particleCount: Int = 100) {
    val particles = remember { List(particleCount) { Particle() } }
    val scope = rememberCoroutineScope()
    var canvasSize by remember { mutableStateOf(IntSize(1000, 1000)) }

    LaunchedEffect(Unit) {
        particles.forEach { particle ->
            scope.launch {
                while (isActive) {
                    particle.update(canvasSize)
                    delay(16L) // ~60 FPS
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { canvasSize = it.size }
            .drawWithContent {
                drawContent()
                particles.forEach { particle ->
                    drawCircle(
                        color = particle.color,
                        radius = particle.size,
                        center = Offset(particle.x.value, particle.y.value)
                    )
                }
            }
    )
}