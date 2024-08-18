package com.example.bonapp.ui.components.dialogs

import TagSelectionDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import com.example.bonapp.domain.model.SearchFilters
import com.example.bonapp.util.RecipeConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    currentFilters: SearchFilters,
    onApplyFilters: (SearchFilters) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentFilters.name) }
    var categories by remember { mutableStateOf(currentFilters.categories) }
    var dietTypes by remember { mutableStateOf(currentFilters.dietTypes) }
    var minPrepTime by remember { mutableStateOf(currentFilters.minPrepTime?.toString() ?: "") }
    var maxPrepTime by remember { mutableStateOf(currentFilters.maxPrepTime?.toString() ?: "") }
    var difficulties by remember { mutableStateOf(currentFilters.difficulties) }

    var showCategoryDialog by remember { mutableStateOf(false) }
    var showDietTypeDialog by remember { mutableStateOf(false) }
    var showDifficultyDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Search Filters") },
        text = {
            LazyColumn {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Recipe Name") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                }

                item {
                    Text("Categories", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { showCategoryDialog = true }) {
                        Text("Select Categories (${categories.size})")
                    }
                }

                item {
                    Text("Diet Types", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { showDietTypeDialog = true }) {
                        Text("Select Diet Types (${dietTypes.size})")
                    }
                }

                item {
                    Text("Prep Time Range (minutes)", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            value = minPrepTime,
                            onValueChange = { minPrepTime = it },
                            label = { Text("Min") },
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        )
                        OutlinedTextField(
                            value = maxPrepTime,
                            onValueChange = { maxPrepTime = it },
                            label = { Text("Max") },
                            modifier = Modifier.weight(1f).padding(start = 8.dp)
                        )
                    }
                }

                item {
                    Text("Difficulty", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { showDifficultyDialog = true }) {
                        Text("Select Difficulties (${difficulties.size})")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onApplyFilters(SearchFilters(
                    name = name,
                    categories = categories,
                    dietTypes = dietTypes,
                    minPrepTime = minPrepTime.toIntOrNull(),
                    maxPrepTime = maxPrepTime.toIntOrNull(),
                    difficulties = difficulties
                ))
            }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showCategoryDialog) {
        CategorySelectionDialog(
            selectedCategories = categories,
            onCategoryToggle = { category ->
                categories = if (category in categories) {
                    categories - category
                } else {
                    categories + category
                }
            },
            onDismiss = { showCategoryDialog = false },
            onConfirm = { showCategoryDialog = false }
        )
    }

    if (showDietTypeDialog) {
        DietTypeSelectionDialog(
            selectedDietTypes = dietTypes,
            onDietTypeToggle = { dietType ->
                dietTypes = if (dietType in dietTypes) {
                    dietTypes - dietType
                } else {
                    dietTypes + dietType
                }
            },
            onDismiss = { showDietTypeDialog = false },
            onConfirm = { showDietTypeDialog = false }
        )
    }

    if (showDifficultyDialog) {
        TagSelectionDialog(
            title = "Select Difficulties",
            tags = mapOf("Difficulty" to RecipeConstants.CATEGORIES["Difficulty"]!!),
            selectedTags = difficulties,
            onTagSelected = { difficulty ->
                difficulties = difficulties + difficulty
            },
            onTagDeselected = { difficulty ->
                difficulties = difficulties - difficulty
            },
            onDismiss = { showDifficultyDialog = false },
            onConfirm = { showDifficultyDialog = false }
        )
    }
}