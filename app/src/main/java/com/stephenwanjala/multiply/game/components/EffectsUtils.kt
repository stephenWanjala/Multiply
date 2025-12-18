package com.stephenwanjala.multiply.game.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 **
 * Applies a neumorphic shadow effect to the component.
 *
 * @param offset The offset distance for the shadow
 * @param blurRadius The blur radius (note: actual blur requires API 31+, this uses alpha for effect)
 * @param shape The shape of the shadow
 * @param lightColor The color of the light shadow
 * @param darkColor The color of the dark shadow
 * @param inverted Whether to invert the shadow direction (for pressed effect)
 */
fun Modifier.neumorphicShadow(
    offset: Dp = 6.dp,
    blurRadius: Dp = 6.dp,
    shape: Shape = RoundedCornerShape(8.dp),
    lightColor: Color = Color.White.copy(alpha = 0.7f),
    darkColor: Color = Color.Black.copy(alpha = 0.2f),
    inverted: Boolean = false
): Modifier = this then NeumorphicShadowElement(
    offset = offset,
    blurRadius = blurRadius,
    shape = shape,
    lightColor = lightColor,
    darkColor = darkColor,
    inverted = inverted
)

/**
 * ModifierNodeElement that creates and updates NeumorphicShadowNode
 */
private data class NeumorphicShadowElement(
    val offset: Dp,
    val blurRadius: Dp,
    val shape: Shape,
    val lightColor: Color,
    val darkColor: Color,
    val inverted: Boolean
) : ModifierNodeElement<NeumorphicShadowNode>() {

    override fun create(): NeumorphicShadowNode {
        return NeumorphicShadowNode(
            offset = offset,
            blurRadius = blurRadius,
            shape = shape,
            lightColor = lightColor,
            darkColor = darkColor,
            inverted = inverted
        )
    }

    override fun update(node: NeumorphicShadowNode) {
        node.offset = offset
        node.blurRadius = blurRadius
        node.shape = shape
        node.lightColor = lightColor
        node.darkColor = darkColor
        node.inverted = inverted
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "neumorphicShadow"
        properties["offset"] = offset
        properties["blurRadius"] = blurRadius
        properties["shape"] = shape
        properties["lightColor"] = lightColor
        properties["darkColor"] = darkColor
        properties["inverted"] = inverted
    }
}

/**
 * DrawModifierNode that performs the neumorphic shadow drawing
 */
private class NeumorphicShadowNode(
    var offset: Dp,
    var blurRadius: Dp,
    var shape: Shape,
    var lightColor: Color,
    var darkColor: Color,
    var inverted: Boolean
) : Modifier.Node(), DrawModifierNode {

    private var cachedOutline: Outline? = null
    private var cachedSize: Size? = null
    private var cachedLayoutDirection: LayoutDirection? = null

    override fun ContentDrawScope.draw() {
        val shadowOffsetPx = offset.toPx()
        val currentSize = size

        // Cache the outline for better performance
        if (cachedOutline == null ||
            cachedSize != currentSize ||
            cachedLayoutDirection != layoutDirection
        ) {
            cachedOutline = shape.createOutline(currentSize, layoutDirection, this)
            cachedSize = currentSize
            cachedLayoutDirection = layoutDirection
        }

        val outline = cachedOutline!!

        // Calculate shadow offsets based on inverted state
        val lightOffset = if (inverted) {
            Offset(shadowOffsetPx, shadowOffsetPx)
        } else {
            Offset(-shadowOffsetPx, -shadowOffsetPx)
        }

        val darkOffset = if (inverted) {
            Offset(-shadowOffsetPx, -shadowOffsetPx)
        } else {
            Offset(shadowOffsetPx, shadowOffsetPx)
        }

        // Draw dark shadow
        translate(left = darkOffset.x, top = darkOffset.y) {
            drawOutline(
                outline = outline,
                color = darkColor,
                alpha = darkColor.alpha
            )
        }

        // Draw light shadow
        translate(left = lightOffset.x, top = lightOffset.y) {
            drawOutline(
                outline = outline,
                color = lightColor,
                alpha = lightColor.alpha
            )
        }

        // Draw the actual content on top of shadows
        drawContent()
    }

    override fun onDetach() {
        // Clear cache when node is detached
        cachedOutline = null
        cachedSize = null
        cachedLayoutDirection = null
    }
}


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