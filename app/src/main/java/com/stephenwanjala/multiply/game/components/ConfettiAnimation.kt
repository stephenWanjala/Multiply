package com.stephenwanjala.multiply.game.components

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.stephenwanjala.multiply.game.utlis.Particle
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


fun Modifier.confettiEffect(particleCount: Int = 100): Modifier = composed {
    val particles = remember { List(particleCount) { Particle() } }
    val scope = rememberCoroutineScope()
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

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

    this
        .drawBehind {
            particles.forEach { particle ->
                drawCircle(
                    color = particle.color,
                    radius = particle.size,
                    center = Offset(particle.x.value, particle.y.value)
                )
            }
        }
        .onGloballyPositioned { layoutCoordinates ->
            canvasSize = layoutCoordinates.size
        }
}
