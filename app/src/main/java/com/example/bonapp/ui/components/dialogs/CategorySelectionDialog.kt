package com.example.bonapp.ui.components.dialogs

import TagSelectionDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bonapp.util.RecipeConstants

@Composable
fun CategorySelectionDialog(
    selectedCategories: List<String>,
    onCategoryToggle: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    TagSelectionDialog(
        title = "Select Categories",
        tags = RecipeConstants.CATEGORIES,
        selectedTags = selectedCategories,
        onTagSelected = onCategoryToggle,
        onTagDeselected = onCategoryToggle,
        onDismiss = onDismiss,
        onConfirm = onConfirm
    )
}