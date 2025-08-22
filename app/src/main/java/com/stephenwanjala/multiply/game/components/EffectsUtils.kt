package com.stephenwanjala.multiply.game.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.neumorphicShadow(
    offset: Dp = 6.dp,
    blurRadius: Dp = 6.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    lightColor: Color = Color.White.copy(alpha = 0.7f),
    darkColor: Color = Color.Black.copy(alpha = 0.2f),
    inverted: Boolean = false
): Modifier =
    this.then(
        Modifier.drawBehind {
            val shadowOffset = offset.toPx()
            val shadowBlur = blurRadius.toPx()
            val alpha = (shadowBlur / 100f).coerceIn(0f, 1f)
            val outline = shape.createOutline(size, layoutDirection, this)

            fun drawShadow(color: Color, offset: Offset) {
                drawOutline(
                    outline = outline,
                    color = color.copy(alpha = alpha),
                    style = Stroke(width = shadowBlur),
//                    topLeft = offset
                )
            }

            if (inverted) {
                drawShadow(darkColor, Offset(shadowOffset, shadowOffset))
                drawShadow(lightColor, Offset(-shadowOffset, -shadowOffset))
            } else {
                drawShadow(lightColor, Offset(-shadowOffset, -shadowOffset))
                drawShadow(darkColor, Offset(shadowOffset, shadowOffset))
            }
        }
    )

// Background Effects
@Composable
fun Modifier.glowingOrbs() =
    this.then(Modifier.drawBehind {
        listOf(
            Pair(0.2f to 0.3f, Color(0xFF8A2BE2)),
            Pair(0.7f to 0.1f, Color(0xFF00F9FF)),
            Pair(0.5f to 0.8f, Color(0xFFFF4081))
        ).forEach { (position, color) ->
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(color.copy(alpha = 0.2f), Color.Transparent),
                    center = Offset(size.width * position.first, size.height * position.second),
                    radius = size.minDimension * 0.2f
                ),
                center = Offset(size.width * position.first, size.height * position.second),
                radius = size.minDimension * 0.15f,
                blendMode = BlendMode.Plus
            )
        }
    })

@Composable
fun Modifier.repeatLiquidBackground() =
    this.drawBehind {
        val liquidColor = Color(0x2200E5FF)
        val patternSize = 100.dp.toPx()
        val path = Path().apply {
            moveTo(0f, patternSize)
            quadraticTo(patternSize / 2, 0f, patternSize, patternSize)
            quadraticTo(patternSize * 1.5f, patternSize * 2, patternSize * 2, patternSize)
        }

        repeat((size.width / patternSize).toInt() + 1) { x ->
            repeat((size.height / patternSize).toInt() + 1) { y ->
                translate(left = x * patternSize, top = y * patternSize) {
                    drawPath(
                        path = path,
                        color = liquidColor,
                        style = Stroke(2.dp.toPx())
                    )
                }
            }
        }
    }