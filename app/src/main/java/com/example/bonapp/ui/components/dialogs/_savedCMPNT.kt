package com.example.bonapp.ui.components.dialogs//package com.example.bonapp.ui.components.dialogs
//
//package com.example.bonapp.ui.components.dialogs
//
//import TagSelectionDialog
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.unit.dp
//import com.example.bonapp.domain.model.Recipe
//import com.example.bonapp.util.RecipeConstants
//
//@Composable
//fun DynamicRecipeComponentEditor(
//    components: List<Recipe.Component>,
//    onComponentsChange: (List<Recipe.Component>) -> Unit
//) {
//    val itemStates = remember { mutableStateListOf(*components.toTypedArray()) }
//    Column(
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        itemStates.forEachIndexed { index, component ->
//            ComponentCard(
//                component = component,
//                onComponentChange = { updatedComponent ->
//                    itemStates[index] = updatedComponent
//                    onComponentsChange(itemStates.toList())
//                },
//                onRemoveComponent = {
//                    itemStates.removeAt(index)
//                    onComponentsChange(itemStates.toList())
//                }
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//        }
//        Button(
//            onClick = {
//                itemStates.add(Recipe.Component("New Component", emptyList()))
//                onComponentsChange(itemStates.toList())
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Icon(Icons.Default.Add, contentDescription = "Add Component")
//            Spacer(modifier = Modifier.width(8.dp))
//            Text("Add Component")
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ComponentCard(
//    component: Recipe.Component,
//    onComponentChange: (Recipe.Component) -> Unit,
//    onRemoveComponent: () -> Unit
//) {
//    var currentComponent by remember { mutableStateOf(component) }
//    LaunchedEffect(component) {
//        currentComponent = component
//    }
//    Card(
//        modifier = Modifier.fillMaxWidth().padding(16.dp),
//    ) {
//        Column(modifier = Modifier) {
//            OutlinedTextField(
//                value = currentComponent.title ?: "",
//                onValueChange = { onComponentChange(currentComponent.copy(title = it)) },
//                label = { Text("Component Name") },
//                modifier = Modifier.fillMaxWidth()
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            IngredientList(
//                ingredients = currentComponent.ingredients,
//                onIngredientsChange = { onComponentChange(currentComponent.copy(ingredients = it)) }
//            )
//            Button(
//                onClick = onRemoveComponent,
//                modifier = Modifier.align(Alignment.End)
//            ) {
//                Text("Remove Component")
//            }
//        }
//    }
//}
//
//@Composable
//fun IngredientList(
//    ingredients: List<Recipe.Ingredient>,
//    onIngredientsChange: (List<Recipe.Ingredient>) -> Unit
//) {
//    val itemStates = remember { mutableStateListOf(*ingredients.toTypedArray()) }
//    Column {
//        itemStates.forEachIndexed { index, ingredient ->
//            IngredientRow(
//                ingredient = ingredient,
//                onIngredientChange = { updatedIngredient ->
//                    itemStates[index] = updatedIngredient
//                    onIngredientsChange(itemStates.toList())
//                },
//                onRemoveIngredient = {
//                    itemStates.removeAt(index)
//                    onIngredientsChange(itemStates.toList())
//                }
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//        }
//        Button(
//            onClick = {
//                itemStates.add(Recipe.Ingredient("", "", ""))
//                onIngredientsChange(itemStates.toList())
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("Add Ingredient")
//        }
//    }
//}
//
//@Composable
//fun IngredientRow(
//    ingredient: Recipe.Ingredient,
//    onIngredientChange: (Recipe.Ingredient) -> Unit,
//    onRemoveIngredient: () -> Unit
//) {
//    var currentIngredient by remember { mutableStateOf(ingredient) }
//    LaunchedEffect(ingredient) {
//        currentIngredient = ingredient
//    }
//    var showUnitPicker by remember { mutableStateOf(false) }
//    val isImprecise = RecipeConstants.UNITS["Imprecise"]?.contains(currentIngredient.unit) == true
//
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        OutlinedTextField(
//            value = currentIngredient.name,
//            onValueChange = { onIngredientChange(currentIngredient.copy(name = it)) },
//            label = { Text("Ingredient") },
//            modifier = Modifier.weight(2f)
//        )
//        Spacer(modifier = Modifier.width(4.dp))
//        OutlinedButton(
//            onClick = { showUnitPicker = true },
//            modifier = Modifier.weight(1f)
//        ) {
//            Text(currentIngredient.unit.ifEmpty { "Unit" })
//        }
//        Spacer(modifier = Modifier.width(4.dp))
//        if (!isImprecise) {
//            OutlinedTextField(
//                value = currentIngredient.amount,
//                onValueChange = { onIngredientChange(currentIngredient.copy(amount = it)) },
//                label = { Text("Amount") },
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                modifier = Modifier.weight(1f)
//            )
//            Spacer(modifier = Modifier.width(4.dp))
//        }
//        IconButton(onClick = onRemoveIngredient) {
//            Icon(Icons.Default.Close, contentDescription = "Remove Ingredient")
//        }
//    }
//
//    if (showUnitPicker) {
//        TagSelectionDialog(
//            title = "Select Unit",
//            tags = RecipeConstants.UNITS,
//            selectedTags = listOf(currentIngredient.unit),
//            onTagSelected = {
//                onIngredientChange(currentIngredient.copy(unit = it))
//                showUnitPicker = false
//            },
//            onTagDeselected = { /* Do nothing */ },
//            onDismiss = { showUnitPicker = false },
//            onConfirm = { showUnitPicker = false }
//        )
//    }
//}