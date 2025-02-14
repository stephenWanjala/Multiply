package com.stephenwanjala.multiply.game.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stephenwanjala.multiply.R
import com.stephenwanjala.multiply.game.components.animatedBackground
import com.stephenwanjala.multiply.ui.theme.MultiplyTheme

enum class BubbleMathDifficulty {
    EASY,
    MEDIUM,
    HARD
}

enum class QuizDifficulty(val questionCount: Int, val numberRange: IntRange) {
    BEGINNER(15, 1..20),
    INTERMEDIATE(20, 5..50),
    ADVANCED(25, 10..100),
    EXPERT(30, 1..200)
}

sealed class GameMode {
    data class BubbleMathBlitz(val difficulty: BubbleMathDifficulty) : GameMode()
    data class QuizGenius(val difficulty: QuizDifficulty) : GameMode()
}

@Composable
fun GameModeSelectionScreen(onConfirm: (GameMode) -> Unit) {
    var selectedMode by remember { mutableStateOf<GameMode?>(null) }
    var selectedDifficulty by remember { mutableStateOf<Any?>(null) }

    val gameModes = mapOf(
        "Bubble Math Blitz" to BubbleMathDifficulty.entries,
        "Quiz Genius" to QuizDifficulty.entries
    )

    Box (
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF2A0A45),MaterialTheme.colorScheme.background,MaterialTheme.colorScheme.primary, Color(0xFF0A0420))
                )
            )
            .repeatLiquidBackground()
            .glowingOrbs()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .animatedBackground()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeonText(
                    text = "SELECT MODE",
                    gradient = listOf(Color(0xFF00F9FF), Color(0xFF8A2BE2)),
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.math_mascot),
                    contentDescription = null,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Game Mode Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                gameModes.keys.forEach { modeName ->
                    NeuGameModeCard(
                        mode = modeName,
                        isSelected = selectedMode?.let {
                            when (it) {
                                is GameMode.BubbleMathBlitz -> modeName == "Bubble Math Blitz"
                                is GameMode.QuizGenius -> modeName == "Quiz Genius"
                            }
                        } ?: false,
                        onClick = {
                            selectedMode = when (modeName) {
                                "Bubble Math Blitz" -> GameMode.BubbleMathBlitz(BubbleMathDifficulty.EASY)
                                "Quiz Genius" -> GameMode.QuizGenius(QuizDifficulty.BEGINNER)
                                else -> null
                            }
                            selectedDifficulty = null // Reset difficulty when mode changes
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Difficulty Selection
            selectedMode?.let { mode ->
                val levels = when (mode) {
                    is GameMode.BubbleMathBlitz -> gameModes["Bubble Math Blitz"]!!
                    is GameMode.QuizGenius -> gameModes["Quiz Genius"]!!
                }

                NeuSectionTitle(text = "DIFFICULTY LEVEL")

                LevelSelectionGrid(
                    levels = levels,
                    selectedLevel = selectedDifficulty,
                    onLevelSelected = { level ->
                        selectedDifficulty = level
                        selectedMode = when (mode) {
                            is GameMode.BubbleMathBlitz -> GameMode.BubbleMathBlitz(level as BubbleMathDifficulty)
                            is GameMode.QuizGenius -> GameMode.QuizGenius(level as QuizDifficulty)
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Button
            GradientButton(
                text = "START QUEST",
                enabled = selectedMode != null,
                gradient = listOf(Color(0xFF00E676), Color(0xFF00B8D4)),
                onClick = {
                    selectedMode?.let {
                        onConfirm(it)
                        println("The Game Mode $it")
                    }
                }
            )
        }
    }
}

@Composable
fun NeuGameModeCard(mode: String, isSelected: Boolean, onClick: () -> Unit) {
    val elevation = if (isSelected) 16.dp else 8.dp
    val cardGradient = listOf(Color(0xFF3A1C4A), Color(0xFF1A0F2E))

    Card(
        modifier = Modifier
            .width(200.dp)
            .height(260.dp)
            .neumorphicShadow(
                elevation = elevation,
                shape = RoundedCornerShape(24.dp),
                lightColor = Color(0x44FFFFFF),
                darkColor = Color(0x66000000)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF2A1255) else Color(0xFF1A0A30)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = if (isSelected)
                            listOf(Color(0xFF6A1B9A), Color(0xFF4527A0))
                        else cardGradient
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0x66FFFFFF), Color(0x00FFFFFF))
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(
                        id = if (mode == "Bubble Math Blitz")
                            R.drawable.bubblemuscot else R.drawable.math_mascot
                    ),
                    contentDescription = mode,
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = mode.uppercase(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF00E5FF), Color(0xFFAA00FF))
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (mode == "Bubble Math Blitz")
                        "Fast-paced arithmetic!" else "Strategic challenges!",
                    color = Color(0xFFCCCCCC),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LevelSelectionGrid(
    levels: List<Any>,
    selectedLevel: Any?,
    onLevelSelected: (Any) -> Unit
) {
    val colors = listOf(
        Color(0xFF00C853), // Easy
        Color(0xFFFFD600), // Medium
        Color(0xFFFF3D00), // Hard
        Color(0xFFD50000)  // Expert
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        levels.forEachIndexed { index, level ->
            val isSelected = selectedLevel == level
            Box(
                modifier = Modifier
                    .height(60.dp)
                    .weight(1f)
                    .padding(8.dp)
                    .neumorphicShadow(
                        elevation = if (isSelected) 12.dp else 6.dp,
                        shape = RoundedCornerShape(16.dp),
                        lightColor = Color(0x44FFFFFF),
                        darkColor = Color(0x66000000)
                    )
                    .background(
                        brush = Brush.verticalGradient(
                            colors = if (isSelected)
                                listOf(colors[index], colors[index].copy(alpha = 0.8f))
                            else listOf(Color(0x22FFFFFF), Color(0x11FFFFFF))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0x44FFFFFF), Color(0x00FFFFFF))
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onLevelSelected(level) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (level) {
                        is BubbleMathDifficulty -> level.name
                        is QuizDifficulty -> level.name
                        else -> "Unknown"
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    letterSpacing = 1.2.sp,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Visible
                )
            }
        }
    }
}

// Neon Text Component
@Composable
fun NeonText(
    text: String,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 24.sp
) {
    val brush = remember { Brush.linearGradient(colors = gradient) }

    Box(modifier = modifier) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Transparent,
            style = MaterialTheme.typography.headlineMedium.copy(
                shadow = Shadow(
                    color = gradient.first().copy(alpha = 0.5f),
                    offset = Offset(2f, 2f),
                    blurRadius = 12f
                )
            )
        )
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.headlineMedium.copy(brush = brush)
        )
    }
}

// Gradient Button Component
@Composable
fun GradientButton(
    modifier: Modifier = Modifier,
    text: String,
    gradient: List<Color>,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val buttonGradient = if (enabled) gradient else listOf(Color(0xFF666666), Color(0xFF444444))
    val elevation = if (enabled) 8.dp else 0.dp

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .neumorphicShadow(elevation = elevation),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(buttonGradient),
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.2f),
                            MaterialTheme.colorScheme.background
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

// Neumorphic Section Title
@Composable
fun NeuSectionTitle(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .neumorphicShadow(
                    elevation = 4.dp,
                    shape = RectangleShape,
                    inverted = true
                ),
            color = Color(0x22FFFFFF),
            thickness = 1.dp
        )
        Text(
            text = text,
            color = Color(0xFF00E5FF),
            modifier = Modifier.padding(horizontal = 16.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.1.sp
        )
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .neumorphicShadow(
                    elevation = 4.dp,
                    shape = RectangleShape,
                    inverted = true
                ),
            color = Color(0x22FFFFFF),
            thickness = 1.dp
        )
    }
}

// Neumorphic Shadow Modifier
fun Modifier.neumorphicShadow(
    elevation: Dp,
    shape: Shape = RoundedCornerShape(8.dp),
    lightColor: Color = Color(0x55FFFFFF),
    darkColor: Color = Color(0x55000000),
    inverted: Boolean = false
): Modifier = this.then(
    Modifier.drawBehind {
        val shadowOffset = elevation.toPx()
        val outline = shape.createOutline(size, layoutDirection, this)

        val darkShadowOffset = if (inverted) -shadowOffset else shadowOffset
        val lightShadowOffset = if (inverted) shadowOffset else -shadowOffset

        drawOutline(
            outline = outline,
            color = darkColor,
            style = Fill,
            alpha = 0.6f,
            blendMode = BlendMode.SrcOver
        )

        drawOutline(
            outline = outline,
            color = lightColor,
            style = Fill,
            alpha = 0.6f,
            blendMode = BlendMode.SrcOver
        )
    }
)

// Animated Border Modifier
fun Modifier.animatedBorder(
    brush: Brush,
    shape: Shape = RoundedCornerShape(8.dp),
    borderWidth: Dp = 2.dp
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition()
    val translateX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing)
        )
    )

    this.then(
        Modifier.drawWithCache {
            onDrawBehind {
                val borderWidthPx = borderWidth.toPx()
                val brushHeight = size.height + borderWidthPx * 2

                drawRect(
                    brush = brush,
                    topLeft = Offset(-borderWidthPx + translateX, -borderWidthPx),
                    size = Size(brushHeight, brushHeight),
                    blendMode = BlendMode.SrcIn
                )
            }
        }
    )
}

// Background Effects
@Composable
private fun Modifier.glowingOrbs() =
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
private fun Modifier.repeatLiquidBackground() =
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



@PreviewScreenSizes
@Composable
private fun PreviewGameModes() {
    MultiplyTheme {
        GameModeSelectionScreen(onConfirm = { val1->})
    }
}

