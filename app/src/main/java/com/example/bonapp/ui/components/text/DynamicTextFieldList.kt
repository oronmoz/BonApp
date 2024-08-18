package com.example.bonapp.ui.components.text

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun DynamicTextFieldList(
    items: List<String>,
    onItemsChange: (List<String>) -> Unit,
    label: String,
    icon: ImageVector
) {
    Column {
        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = item,
                    onValueChange = { newValue ->
                        val newList = items.toMutableList()
                        newList[index] = newValue
                        onItemsChange(newList)
                    },
                    label = { Text(label) },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    val newList = items.toMutableList()
                    newList.removeAt(index)
                    onItemsChange(newList)
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        Button(
            onClick = {
                onItemsChange(items + "")
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(imageVector = icon, contentDescription = "Add $label")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add $label")
        }
    }
}