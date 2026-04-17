package com.stephenwanjala.multiply.game.feat_bubblemode

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stephenwanjala.multiply.R
import com.stephenwanjala.multiply.core.designsystem.component.AnimatedFloatingSymbolsBackground
import com.stephenwanjala.multiply.ui.theme.LocalMultiplyColors
import com.stephenwanjala.multiply.ui.theme.MultiplyTheme
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    onPlayClick: () -> Unit,
    onHowToPlayClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: GameViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Bubble Math",
                        style = MaterialTheme.typography.titleLarge,
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
                AnimatedFloatingSymbolsBackground(alpha = 0.35f)

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HeroCard(
                        difficulty = state.selectedDifficulty.name,
                        modifier = Modifier.fillMaxWidth()
                    )

                    HighScoreBanner(
                        highScore = state.highScore,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.weight(1f))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PulsatingPlayButton(
                            onClick = onPlayClick,
                            modifier = Modifier.fillMaxWidth(0.82f)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(0.82f),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DuoButton(
                                text = "How to Play",
                                onClick = onHowToPlayClick,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                leading = Icons.Default.MenuBook,
                                fontSize = 14,
                                height = 52.dp,
                                modifier = Modifier.weight(1f)
                            )
                            DuoButton(
                                text = "Settings",
                                onClick = onSettingsClick,
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                                leading = Icons.Default.Settings,
                                fontSize = 14,
                                height = 52.dp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun HeroCard(difficulty: String, modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        primary.copy(alpha = 0.9f),
                        tertiary.copy(alpha = 0.85f)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedMascot(
                modifier = Modifier.size(110.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                AnimatedGameTitle()
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Pop bubbles. Solve math. Beat your score!",
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.25f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = difficulty.lowercase().replaceFirstChar { it.titlecase() },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun HighScoreBanner(highScore: Int, modifier: Modifier = Modifier) {
    val star = LocalMultiplyColors.current.star
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleIconBadge(
            icon = Icons.Default.EmojiEvents,
            backgroundColor = star,
            contentColor = Color.White,
            size = 44.dp
        )
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "HIGH SCORE",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = if (highScore == 0) "Start your streak!" else "$highScore points",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
        }
        Text(
            text = if (highScore == 0) "🚀" else "🔥",
            fontSize = 26.sp
        )
    }
}

@Composable
private fun PulsatingPlayButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val scale = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.04f,
            animationSpec = infiniteRepeatable(
                animation = tween(700, easing = EaseInOutQuad),
                repeatMode = RepeatMode.Reverse
            )
        )
    }
    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
            .wrapContentSize()
    ) {
        DuoButton(
            text = "PLAY!",
            onClick = onClick,
            containerColor = LocalMultiplyColors.current.success,
            contentColor = Color.White,
            leading = Icons.Default.PlayArrow,
            fontSize = 24,
            height = 68.dp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AnimatedGameTitle() {
    var logoScale by remember { mutableFloatStateOf(0.5f) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(1f, animationSpec = tween(420, easing = EaseInOutQuad))
        animate(
            initialValue = 0.5f,
            targetValue = 1f,
            animationSpec = tween(650, easing = EaseOutBack)
        ) { value, _ -> logoScale = value }
    }

    Text(
        text = "Math Master!",
        fontSize = 28.sp,
        fontWeight = FontWeight.Black,
        color = Color.White,
        textAlign = TextAlign.Start,
        lineHeight = 32.sp,
        modifier = Modifier
            .graphicsLayer {
                scaleX = logoScale
                scaleY = logoScale
                alpha = alphaAnim.value
            }
            .widthIn(min = 150.dp)
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
            .background(Color.White.copy(alpha = 0.22f))
            .padding(8.dp)
            .graphicsLayer {
                scaleX = scaleAnim.value
                scaleY = scaleAnim.value
                rotationZ = rotationAnim.value
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin.Center
            }
    )
}


@PreviewLightDark
@PreviewScreenSizes
@Composable
private fun PreviewWelcomeScreen() {
    MultiplyTheme {
        WelcomeScreenPreview()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WelcomeScreenPreview() {
    Scaffold { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AnimatedFloatingSymbolsBackground(alpha = 0.3f)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HeroCard(difficulty = "MEDIUM", modifier = Modifier.fillMaxWidth())
                    HighScoreBanner(highScore = 42, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.weight(1f))
                    PulsatingPlayButton(onClick = {}, modifier = Modifier.fillMaxWidth(0.82f))
                    Row(
                        modifier = Modifier.fillMaxWidth(0.82f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        DuoButton(
                            text = "How to Play",
                            onClick = {},
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            leading = Icons.Default.MenuBook,
                            fontSize = 14,
                            height = 52.dp,
                            modifier = Modifier.weight(1f)
                        )
                        DuoButton(
                            text = "Settings",
                            onClick = {},
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            leading = Icons.Default.Settings,
                            fontSize = 14,
                            height = 52.dp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
