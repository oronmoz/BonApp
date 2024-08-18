//package com.example.bonapp.ui.components.forms
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.example.bonapp.domain.model.Recipe
//import com.example.bonapp.ui.viewmodel.RecipeViewModel
//
//@Composable
//fun RecipeForm(
//    recipe: Recipe,
//    onSave: () -> Unit,
//    onNavigateBack: () -> Unit,
//    viewModel: RecipeViewModel
//) {
//    val recipeState by viewModel.recipeState.collectAsState()
//    val isImageUploading by viewModel.isImageUploading.collectAsState()
//    val imageUploadError by viewModel.imageUploadError.collectAsState()
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        RecipeFormFields(
//            recipe = recipe,
//            onRecipeFieldChange = viewModel::updateRecipeField,
//            onAddComponent = viewModel::addComponent,
//            onUpdateComponent = viewModel::updateComponent,
//            onRemoveComponent = viewModel::removeComponent,
//            onAddInstruction = viewModel::addInstruction,
//            onUpdateInstruction = viewModel::updateInstruction,
//            onRemoveInstruction = viewModel::removeInstruction,
//            onUploadImage = viewModel::uploadImage,
//            isImageUploading = isImageUploading,
//            imageUploadError = imageUploadError
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(
//            onClick = onSave,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(if (recipe.id.isEmpty()) "Add Recipe" else "Update Recipe")
//        }
//
//        when (recipeState) {
//            is RecipeViewModel.RecipeState.Loading -> CircularProgressIndicator()
//            is RecipeViewModel.RecipeState.Error -> Text(
//                text = (recipeState as RecipeViewModel.RecipeState.Error).message,
//                color = MaterialTheme.colorScheme.error
//            )
//            is RecipeViewModel.RecipeState.Success -> LaunchedEffect(Unit) { onNavigateBack() }
//            else -> {}
//        }
//    }
//}