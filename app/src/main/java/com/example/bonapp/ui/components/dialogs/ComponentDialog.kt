import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.bonapp.domain.model.Recipe
import com.example.bonapp.ui.components.buttons.IngredientRow
import com.example.bonapp.ui.components.dialogs.UnitSelectionDialog
import com.example.bonapp.util.RecipeConstants

@Composable
fun ComponentDialog(
    onDismiss: () -> Unit,
    onConfirm: (Recipe.Component) -> Unit,
    existingComponent: Recipe.Component? = null
) {
    var name by remember { mutableStateOf(existingComponent?.title ?: "") }
    var ingredients by remember { mutableStateOf(existingComponent?.ingredients ?: listOf(Recipe.Ingredient("", "", ""))) }
    var showUnitSelectionDialog by remember { mutableStateOf(false) }
    var currentEditingIngredientIndex by remember { mutableStateOf<Int?>(null) }

    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onDismissRequest = onDismiss,
        title = { Text(if (existingComponent == null) "Add Component" else "Edit Component") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Component Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(ingredients) { index, ingredient ->
                        IngredientRow(
                            ingredient = ingredient,
                            onIngredientChange = { newIngredient ->
                                ingredients = ingredients.toMutableList().apply { this[index] = newIngredient }
                            },
                            onRemove = {
                                if (ingredients.size > 1) {
                                    ingredients = ingredients.toMutableList().apply { removeAt(index) }
                                }
                            },
                            onUnitClick = {
                                currentEditingIngredientIndex = index
                                showUnitSelectionDialog = true
                            }
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    item {
                        Button(
                            onClick = { ingredients = ingredients + Recipe.Ingredient("", "", "") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Ingredient")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Ingredient")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(Recipe.Component(name, ingredients))
                    onDismiss()
                },
                enabled = name.isNotBlank() && ingredients.isNotEmpty() && ingredients.all { it.name.isNotBlank() }
            ) {
                Text(if (existingComponent == null) "Add Component" else "Update Component")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showUnitSelectionDialog) {
        UnitSelectionDialog(
            selectedUnit = ingredients[currentEditingIngredientIndex!!].unit,
            onUnitSelected = { unit ->
                ingredients = ingredients.toMutableList().apply {
                    this[currentEditingIngredientIndex!!] = this[currentEditingIngredientIndex!!].copy(unit = unit)
                }
                showUnitSelectionDialog = false
            },
            onDismiss = { showUnitSelectionDialog = false }
        )
    }
}