package com.example.bonapp.ui.recipes

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bonapp.ui.components.dialogs.RecipePreviewScreen
import com.example.bonapp.ui.components.forms.RecipeFormFields
import com.example.bonapp.ui.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(
    onNavigateBack: () -> Unit,
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val recipe by viewModel.currentRecipe.collectAsState()
    val recipeState by viewModel.recipeState.collectAsState()
    val isImageUploading by viewModel.isImageUploading.collectAsState()
    val imageUploadError by viewModel.imageUploadError.collectAsState()
    val hasUnsavedChanges by viewModel.hasUnsavedChanges.collectAsState()
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.createNewRecipe()
    }

    BackHandler(enabled = hasUnsavedChanges) {
        showConfirmationDialog = true
    }

    if (showPreview) {
        recipe?.let {
            RecipePreviewScreen(
                recipe = it,
                onClose = { showPreview = false }
            )
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Add Recipe") },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (hasUnsavedChanges) {
                                showConfirmationDialog = true
                            } else {
                                onNavigateBack()
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            bottomBar = {
                BottomAppBar {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { showPreview = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Preview Recipe")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = { viewModel.saveRecipe() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Save Recipe")
                        }
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                when (recipeState) {
                    is RecipeViewModel.RecipeState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    is RecipeViewModel.RecipeState.Error -> {
                        Text(
                            text = (recipeState as RecipeViewModel.RecipeState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                    else -> {
                        recipe?.let { currentRecipe ->
                            RecipeFormFields(
                                recipe = currentRecipe,
                                onRecipeFieldChange = viewModel::updateRecipeField,
                                onAddComponent = viewModel::addComponent,
                                onUpdateComponent = viewModel::updateComponent,
                                onRemoveComponent = viewModel::removeComponent,
                                onAddInstruction = viewModel::addInstruction,
                                onUpdateInstruction = viewModel::updateInstruction,
                                onRemoveInstruction = viewModel::removeInstruction,
                                onUploadImage = viewModel::uploadImage,
                                isImageUploading = isImageUploading,
                                imageUploadError = imageUploadError,
                                onRemoveImage = viewModel::removeImage
                            )
                        } ?: Text("No recipe data available")
                    }
                }
            }
        }

        if (showConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                title = { Text("Unsaved Changes") },
                text = { Text("You have unsaved changes. Are you sure you want to leave?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.resetUnsavedChanges()
                        onNavigateBack()
                    }) {
                        Text("Leave")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmationDialog = false }) {
                        Text("Stay")
                    }
                }
            )
        }
    }
}