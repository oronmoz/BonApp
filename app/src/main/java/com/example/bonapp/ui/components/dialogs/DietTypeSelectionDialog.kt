package com.example.bonapp.ui.components.dialogs

import TagSelectionDialog
import androidx.compose.runtime.Composable
import com.example.bonapp.util.RecipeConstants

@Composable
fun DietTypeSelectionDialog(
    selectedDietTypes: List<String>,
    onDietTypeToggle: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    TagSelectionDialog(
        title = "Select Diet Types",
        tags = mapOf("Diet Types" to RecipeConstants.DIET_TYPES),
        selectedTags = selectedDietTypes,
        onTagSelected = onDietTypeToggle,
        onTagDeselected = onDietTypeToggle,
        onDismiss = onDismiss,
        onConfirm = onConfirm
    )
}