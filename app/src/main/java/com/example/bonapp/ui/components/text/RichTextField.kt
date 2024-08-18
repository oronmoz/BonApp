package com.example.bonapp.ui.components.text

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun RichTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    var isBold by remember { mutableStateOf(false) }
    val fontSize by remember { mutableStateOf(16.sp) }

    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                fontSize = fontSize
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { isBold = !isBold }) {
                Text("Toggle Bold")
            }
            Button(onClick = { fontSize.value.inc() }) {
                Text("Increase Font Size")
            }
            Button(onClick = { fontSize.value.dec() }) {
                Text("Decrease Font Size")
            }
        }
    }
}