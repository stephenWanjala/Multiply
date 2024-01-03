package com.github.stephenwanjala.multiply.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VirtualKeyboard(modifier:Modifier=Modifier,onNumberClick: (Int) -> Unit, onEnterClick: () -> Unit) {
    // Define a list of numbers to display on the keyboard
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0)

    // Use a grid layout to arrange the buttons
    Column(modifier = modifier.fillMaxWidth()) {
        numbers.chunked(3).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                row.forEach { number ->
                    // Use a button to display each number
                    TextButton(onClick = { onNumberClick(number) }, modifier = Modifier.size(64.dp)) {
                        Text(text = number.toString(), fontSize = 24.sp)
                    }
                }
            }
        }
        // Use a button to display the enter key
        Button(onClick = onEnterClick, modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)) {
            Text(text = "Enter", fontSize = 24.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun KeyboardPrev() {
    VirtualKeyboard(onNumberClick = {}, onEnterClick = {})
}