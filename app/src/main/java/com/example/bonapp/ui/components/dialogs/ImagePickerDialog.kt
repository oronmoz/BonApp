package com.example.bonapp.ui.components.dialogs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ImagePickerDialog(
    onImageSelected: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Profile Picture") },
        text = { Text("Choose a new profile picture from your device") },
        confirmButton = {
            TextButton(onClick = { launcher.launch("image/*") }) {
                Text("Choose Image")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}