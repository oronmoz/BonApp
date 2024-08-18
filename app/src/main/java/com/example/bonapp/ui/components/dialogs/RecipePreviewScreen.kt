package com.example.bonapp.ui.components.dialogs

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.bonapp.domain.model.Recipe
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.bonapp.ui.recipes.RecipeDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipePreviewScreen(
    recipe: Recipe,
    onClose: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe Preview") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close Preview")
                    }
                }
            )
        }
    ) { paddingValues ->
        RecipeDetails(
            recipe = recipe,
            onAuthorClick = { /* No-op in preview */ },
            onTagClick = { /* No-op in preview */ },
            modifier = Modifier.padding(paddingValues).padding(16.dp)
        )
    }
}

@Composable
fun RecipeDetails(
    recipe: Recipe,
    onAuthorClick: (String) -> Unit,
    onTagClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(recipe.name, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Display other recipe details here...
        // This is a simplified version, you should include all relevant recipe information
        Text("Prep Time: ${recipe.prepTime ?: "N/A"} minutes")
        Text("Total Time: ${recipe.totalTime} minutes")
        Text("Yields: ${recipe.yields}")

        // Add more details as needed...
    }
}