package com.example.bonapp.ui.components.dialogs

import TagSelectionDialog
import androidx.compose.runtime.Composable
import com.example.bonapp.util.RecipeConstants

@Composable
fun UnitSelectionDialog(
    selectedUnit: String,
    onUnitSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    TagSelectionDialog(
        title = "Select Unit",
        tags = RecipeConstants.UNITS,
        selectedTags = listOf(selectedUnit),
        onTagSelected = { onUnitSelected(it) },
        onTagDeselected = { },
        onDismiss = onDismiss,
        onConfirm = onDismiss
    )
}