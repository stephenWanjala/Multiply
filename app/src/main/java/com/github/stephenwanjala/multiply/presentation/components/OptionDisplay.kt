package com.github.stephenwanjala.multiply.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.stephenwanjala.multiply.ui.theme.MultiplyTheme

@Composable
fun OptionsDisplay(modifier: Modifier= Modifier, options: List<Int>, onOptionClick: (Int) -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Choose the correct answer:")
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            options.forEach { option ->
                OptionButton(option = option, onClick = { onOptionClick(option) })
            }
        }
    }
}

@Composable
fun OptionButton(option: Int, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(text = option.toString())

    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun OptionsDisplayPrev() {
    MultiplyTheme {
        OptionsDisplay(options = listOf(1, 2, 3, 4), onOptionClick = {})
    }
}