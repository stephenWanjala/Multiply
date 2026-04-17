package com.stephenwanjala.multiply.game.feat_bubblemode

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Duolingo-style chunky button: solid colored top face with a darker
 * "stripe" along the bottom to give the tactile 3D look. When pressed,
 * the top face slides down onto the stripe for the satisfying click feel.
 */
@Composable
fun DuoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    enabled: Boolean = true,
    height: Dp = 60.dp,
    fontSize: Int = 20,
    leading: ImageVector? = null,
    shape: Shape = RoundedCornerShape(18.dp)
) {
    val interaction = remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val lift by animateFloatAsState(
        targetValue = if (isPressed || !enabled) 0f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow),
        label = "duoLift"
    )
    val bottom = containerColor.darken(0.28f)
    val face = if (enabled) containerColor else containerColor.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .height(height)
            .defaultMinSize(minWidth = 120.dp)
    ) {
        // The shadow stripe (bottom darker slab)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(shape)
                .background(bottom)
        )
        // The face — pressed = slides onto stripe
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height - 6.dp)
                .graphicsLayer { translationY = -6.dp.toPx() * lift }
                .clip(shape)
                .background(face)
                .clickable(
                    interactionSource = interaction,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (leading != null) {
                    Icon(
                        imageVector = leading,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(
                    text = text,
                    color = contentColor,
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

/**
 * A compact pill chip used for HUD-style stats (score, hearts, etc).
 */
@Composable
fun StatChip(
    label: String,
    value: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(containerColor)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
        )
        Column {
            Text(
                text = label.uppercase(),
                color = contentColor.copy(alpha = 0.75f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Text(
                text = value,
                color = contentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

/**
 * A playful rounded card that carries a splash of color on its edge.
 */
@Composable
fun DuoCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(containerColor)
            .border(2.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(contentPadding)
    ) {
        content()
    }
}

/**
 * Circular icon badge used on cards / list rows.
 */
@Composable
fun CircleIconBadge(
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color = Color.White,
    size: Dp = 44.dp,
    iconSize: Dp = 24.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * A soft gradient brush useful for playful hero backgrounds.
 */
@Composable
fun heroGradient(primary: Color, secondary: Color): Brush =
    Brush.linearGradient(colors = listOf(primary, secondary))

/**
 * Returns a darkened version of the given color by the given fraction [0..1].
 * Used to render the Duo button's drop stripe.
 */
fun Color.darken(fraction: Float = 0.2f): Color {
    val f = fraction.coerceIn(0f, 1f)
    return Color(
        red = (red * (1 - f)).coerceIn(0f, 1f),
        green = (green * (1 - f)).coerceIn(0f, 1f),
        blue = (blue * (1 - f)).coerceIn(0f, 1f),
        alpha = alpha
    )
}

/** Picks black or white text depending on background luminance. */
fun Color.readableOn(): Color =
    if (luminance() > 0.55f) Color(0xFF1D1B1E) else Color.White
