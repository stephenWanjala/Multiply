package com.github.stephenwanjala.multiply.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.stephenwanjala.multiply.presentation.components.MathBubbleGame
import com.ramcosta.composedestinations.annotation.Destination
import kotlin.random.Random


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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MathBubbleGame()
            }
        }

    }

}




fun generateMathProblem(): String {
    val operand1 = Random.nextInt(1, 10)
    val operand2 = Random.nextInt(1, 10)
    val operator = when (Random.nextBoolean()) {
        true -> "+"
        false -> "-"
    }
    return "$operand1 $operator $operand2 = ?"
}