package com.example.bonapp.ui.components.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InstructionDialog(
    instruction: String? = null,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var instructionText by remember { mutableStateOf(instruction ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (instruction == null) "Add Instruction" else "Edit Instruction") },
        text = {
            Column {
                OutlinedTextField(
                    value = instructionText,
                    onValueChange = { instructionText = it },
                    label = { Text("Instruction") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(instructionText)
                    onDismiss()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}