package com.example.bonapp.ui.components.buttons

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ImageUploadButton(
    onImageSelected: (Uri) -> Unit,
    onImageRemoved: (Uri) -> Unit,
    isLoading: Boolean,
    error: String?,
    selectedImages: List<Uri>
) {
    var showImagePicker by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        uris.forEach { onImageSelected(it) }
    }

    Column {
        Button(
            onClick = { showImagePicker = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Icon(Icons.Default.AddAPhoto, contentDescription = "Upload Images")
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text("Upload Images")
            }
        }

        if (selectedImages.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedImages) { uri ->
                    Box {
                        AsyncImage(
                            model = uri,
                            contentDescription = "Uploaded image",
                            modifier = Modifier.size(80.dp),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { onImageRemoved(uri) },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove image",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

    if (showImagePicker) {
        launcher.launch("image/*")
        showImagePicker = false
    }
}