package com.example.bonapp.ui.components.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    min: Int = 0,
    max: Int = Int.MAX_VALUE,
    step: Int = 5
) {
    var textValue by remember(value) { mutableStateOf(value.toString()) }

    Column (horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(IntrinsicSize.Min).padding(top = 4.dp, bottom = 8.dp),
        ) {
            IconButton(
                onClick = {
                    val newValue = (value - step).coerceAtLeast(min)
                    onValueChange(newValue)
                    textValue = newValue.toString()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("-", textAlign = TextAlign.Center)
            }
            OutlinedTextField(
                label = { Text(label) },
                value = textValue,
                onValueChange = {
                    textValue = it
                    it.toIntOrNull()?.let { intValue ->
                        if (intValue in min..max) {
                            onValueChange(intValue)
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .weight(2f)
                    .padding(horizontal = 8.dp),
                singleLine = true
            )
            IconButton(
                onClick = {
                    val newValue = (value + step).coerceAtMost(max)
                    onValueChange(newValue)
                    textValue = newValue.toString()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("+")
            }
        }
    }
}