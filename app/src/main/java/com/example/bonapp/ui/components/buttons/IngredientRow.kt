package com.example.bonapp.ui.components.buttons

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.bonapp.domain.model.Recipe
import com.example.bonapp.util.RecipeConstants

@Composable
fun IngredientRow(
    ingredient: Recipe.Ingredient,
    onIngredientChange: (Recipe.Ingredient) -> Unit,
    onRemove: () -> Unit,
    onUnitClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = ingredient.name,
            onValueChange = { onIngredientChange(ingredient.copy(name = it)) },
            label = { Text("Ingredient") },
            modifier = Modifier.weight(2f),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(4.dp))
        OutlinedButton(
            onClick = onUnitClick,
            modifier = Modifier.weight(1f)
        ) {
            Text(ingredient.unit.ifEmpty { "Unit" }, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.width(4.dp))
        OutlinedTextField(
            value = ingredient.amount,
            onValueChange = { onIngredientChange(ingredient.copy(amount = it)) },
            label = { Text("Amount") },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Remove Ingredient")
        }
    }
}