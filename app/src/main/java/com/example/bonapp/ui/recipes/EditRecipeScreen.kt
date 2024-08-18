package com.example.bonapp.ui.recipes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bonapp.ui.components.forms.RecipeFormFields
import com.example.bonapp.ui.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecipeScreen(
    recipeId: String,
    onNavigateBack: () -> Unit,
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val recipe by viewModel.currentRecipe.collectAsState()
    val recipeState by viewModel.recipeState.collectAsState()
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val isImageUploading by viewModel.isImageUploading.collectAsState()
    val imageUploadError by viewModel.imageUploadError.collectAsState()

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Recipe") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Recipe")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
            }

            Button(
                onClick = {
                    viewModel.saveRecipe()
                    onNavigateBack()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Update Recipe")
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Recipe") },
            text = { Text("Are you sure you want to delete this recipe?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        recipe?.let {
                            viewModel.deleteRecipe(it)
                            onNavigateBack()
                        }
                        showDeleteConfirmation = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}