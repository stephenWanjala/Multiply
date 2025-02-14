package com.stephenwanjala.multiply.game.feat_quizmode

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Bottom
import androidx.compose.foundation.layout.WindowInsetsSides.Companion.Horizontal
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stephenwanjala.multiply.ui.theme.MultiplyTheme

@Composable
fun QuestionsScreen(viewModel: QuestionsViewModel,onClosePressed:()->Unit) {
    val state =viewModel.state.collectAsStateWithLifecycle().value
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
            QuestionsTopAppBar(
                modifier = Modifier,
                onClosePressed = onClosePressed,
                currentQuestionIndex = state.currentQuestionIndex,
                totalCount = state.questions.count()
            )
        }) { paddingValues ->

        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsTopAppBar(
    modifier: Modifier = Modifier,
    onClosePressed: () -> Unit,
    currentQuestionIndex: Int,
    totalCount: Int
) {
    Column(modifier = modifier.fillMaxWidth()) {

        CenterAlignedTopAppBar(
            title = {
                TopAppBarTitle(
                    questionIndex = currentQuestionIndex,
                    totalQuestionsCount = totalCount,
                )
            },
            actions = {
                IconButton(
                    onClick = onClosePressed,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface.copy(.67f)
                    )
                }
            }
        )

        val animatedProgress by animateFloatAsState(
            targetValue = (currentQuestionIndex + 1) / totalCount.toFloat(),
            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
        )
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
        )
    }
}


@Composable
private fun TopAppBarTitle(
    questionIndex: Int,
    totalQuestionsCount: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(
            text = (questionIndex + 1).toString(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = "of $totalQuestionsCount",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    }
}

@Composable
fun QuestionBottomBar(
    shouldShowPreviousButton: Boolean,
    shouldShowDoneButton: Boolean,
    isNextButtonEnabled: Boolean,
    onPreviousPressed: () -> Unit,
    onNextPressed: () -> Unit,
    onDonePressed: () -> Unit
) {
    Surface(shadowElevation = 7.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Since we're not using a Material component but we implement our own bottom bar,
                // we will also need to implement our own edge-to-edge support. Similar to the
                // NavigationBar, we add the horizontal and bottom padding if it hasn't been consumed yet.
                .windowInsetsPadding(WindowInsets.systemBars.only(Horizontal + Bottom))
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            if (shouldShowPreviousButton) {
                OutlinedButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = onPreviousPressed
                ) {
                    Text(text = "Previous")
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            if (shouldShowDoneButton) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = onDonePressed,
                    enabled = isNextButtonEnabled,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "Done")
                }
            } else {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    onClick = onNextPressed,
                    enabled = isNextButtonEnabled,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "Next")
                }
            }
        }
    }
}
@PreviewLightDark
@Composable
private fun QuestionsScreenPreview() {
    MultiplyTheme {
//        QuestionsScreen(hiltViewModel())
    }
}