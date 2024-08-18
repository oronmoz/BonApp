package com.example.bonapp.ui.components.forms

import ComponentDialog
import TagSelectionDialog
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Blender
import androidx.compose.material.icons.filled.Copyright
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bonapp.domain.model.Recipe
import com.example.bonapp.ui.components.buttons.ImageUploadButton
import com.example.bonapp.ui.components.buttons.NumberPicker
import com.example.bonapp.ui.components.dialogs.CategorySelectionDialog
import com.example.bonapp.ui.components.dialogs.ComponentCard
import com.example.bonapp.ui.components.dialogs.DietTypeSelectionDialog
import com.example.bonapp.ui.components.text.DynamicTextFieldList
import com.example.bonapp.ui.viewmodel.RecipeViewModel
import com.example.bonapp.util.RecipeConstants

@Composable
fun RecipeFormFields(
    recipe: Recipe,
    onRecipeFieldChange: (RecipeViewModel.RecipeField, Any) -> Unit,
    onAddComponent: (Recipe.Component) -> Unit,
    onUpdateComponent: (Int, Recipe.Component) -> Unit,
    onRemoveComponent: (Int) -> Unit,
    onAddInstruction: (String) -> Unit,
    onUpdateInstruction: (Int, String) -> Unit,
    onRemoveInstruction: (Int) -> Unit,
    onUploadImage: (Uri) -> Unit,
    onRemoveImage: (Uri) -> Unit,
    isImageUploading: Boolean,
    imageUploadError: String?
) {
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showDietTypeDialog by remember { mutableStateOf(false) }
    var showToolsDialog by remember { mutableStateOf(false) }
    var showComponentDialog by remember { mutableStateOf(false) }
    var currentEditingComponentIndex by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        // Recipe Name
        Text("Recipe Title", style = MaterialTheme.typography.titleMedium)
        Text("Give your recipe a descriptive and appealing name.", style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(
            value = recipe.name,
            onValueChange = { onRecipeFieldChange(RecipeViewModel.RecipeField.NAME, it) },
            label = { Text("Recipe Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Categories
        Text("Recipe Categories", style = MaterialTheme.typography.titleMedium)
        Text("Select the categories that best fit your recipe.", style = MaterialTheme.typography.bodySmall)
        Button(onClick = { showCategoryDialog = true }) {
            Icon(Icons.Default.LocalOffer, contentDescription = "Select Categories")
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text("Select Categories (${recipe.categories.size})")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Diet Types
        Text("Dietary Preferences", style = MaterialTheme.typography.titleMedium)
        Text("Choose the diet types that your recipe accommodates.", style = MaterialTheme.typography.bodySmall)
        Button(onClick = { showDietTypeDialog = true }) {
            Icon(Icons.Default.LocalDining, contentDescription = "Select Categories")
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text("Select Diet Types (${recipe.dietTypes.size})")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Sponsorship
        Text("Sponsorship", style = MaterialTheme.typography.titleMedium)
        Text("Mention any sponsorships or partnerships for this recipe.", style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(
            value = recipe.sponsorship ?: "",
            onValueChange = { onRecipeFieldChange(RecipeViewModel.RecipeField.SPONSORSHIP, it) },
            label = { Text("Sponsorship") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Image Upload
        Text("Recipe Photos", style = MaterialTheme.typography.titleMedium)
        Text("Upload appealing photos of your recipe.", style = MaterialTheme.typography.bodySmall)
        ImageUploadButton(
            onImageSelected = { uri ->
                onUploadImage(uri)
            },
            onImageRemoved = { uri ->
                onRemoveImage(uri)
            },
            isLoading = isImageUploading,
            error = imageUploadError,
            selectedImages = recipe.pictureUrls.map { Uri.parse(it) }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Video Link
        Text("Recipe Video", style = MaterialTheme.typography.titleMedium)
        Text("Add a link to a video showcasing your recipe (optional).", style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(
            value = recipe.videoLink ?: "",
            onValueChange = { onRecipeFieldChange(RecipeViewModel.RecipeField.VIDEO_LINK, it) },
            label = { Text("Video Link") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // About
        Text("Recipe Description", style = MaterialTheme.typography.titleMedium)
        Text("Provide a brief overview of your recipe.", style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(
            value = recipe.about ?: "",
            onValueChange = { onRecipeFieldChange(RecipeViewModel.RecipeField.ABOUT, it) },
            label = { Text("About") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Inspired By Links
        Text("Inspired By Links", style = MaterialTheme.typography.titleMedium)
        Text("Credit the sources that inspired your recipe (optional).", style = MaterialTheme.typography.bodySmall)
        recipe.inspiredByLinks?.let {
            DynamicTextFieldList(
                items = it,
                onItemsChange = { newLinks ->
                    onRecipeFieldChange(RecipeViewModel.RecipeField.INSPIRED_BY_LINKS, newLinks)
                },
                label = "Credit Your Inspirations",
                icon = Icons.Default.Copyright
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Prep Time and Total Time
        Text("Preparation Time", style = MaterialTheme.typography.titleMedium)
        Text("Enter the time required for prep and total cooking.", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))
        NumberPicker(
            value = recipe.prepTime ?: 0,
            onValueChange = { prepTime ->
                onRecipeFieldChange(RecipeViewModel.RecipeField.PREP_TIME, prepTime)
                if (recipe.totalTime < prepTime) {
                    onRecipeFieldChange(RecipeViewModel.RecipeField.TOTAL_TIME, prepTime)
                }
            },
            label = "Prep Time (minutes)",
        )
        Spacer(modifier = Modifier.height(8.dp))
        NumberPicker(
            value = recipe.totalTime,
            onValueChange = { totalTime ->
                val prepTime = recipe.prepTime ?: 0
                onRecipeFieldChange(RecipeViewModel.RecipeField.TOTAL_TIME, maxOf(totalTime, prepTime))
            },
            label = "Total Time (minutes)",
            min = recipe.prepTime ?: 0
        )


        Spacer(modifier = Modifier.height(16.dp))

        // Yields and Calories
        Text("Yields and Calories", style = MaterialTheme.typography.titleMedium)
        Text("Specify the number of servings and calories per serving.", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))
        NumberPicker(
            value = recipe.yields.toIntOrNull() ?: 0,
            onValueChange = { onRecipeFieldChange(RecipeViewModel.RecipeField.YIELDS, it.toString()) },
            label = "Yields"
        )
        Spacer(modifier = Modifier.height(8.dp))
        NumberPicker(
            value = recipe.caloriesPerServing ?: 0,
            onValueChange = { onRecipeFieldChange(RecipeViewModel.RecipeField.CALORIES_PER_SERVING, it) },
            label = "Calories per Serving"
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Needed Tools
        Text("Required Tools", style = MaterialTheme.typography.titleMedium)
        Text("Select the tools needed to make this recipe.", style = MaterialTheme.typography.bodySmall)
        Button(onClick = { showToolsDialog = true }) {
            Icon(Icons.Default.Blender, contentDescription = "Add Component")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Select Tools (${recipe.neededTools?.size ?: 0})")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Components
        Text("Ingredients", style = MaterialTheme.typography.titleMedium)
        Text("Add the ingredients for each component of the recipe.", style = MaterialTheme.typography.bodySmall)
        recipe.components.forEachIndexed { index, component ->
            ComponentCard(
                component = component,
                onEditComponent = {
                    currentEditingComponentIndex = index
                    showComponentDialog = true
                },
                onRemoveComponent = {
                    onRecipeFieldChange(RecipeViewModel.RecipeField.COMPONENTS,
                        recipe.components.toMutableList().apply { removeAt(index) }
                    )
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Button(
            onClick = {
                currentEditingComponentIndex = null
                showComponentDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.SoupKitchen, contentDescription = "Add Component")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Component")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Instructions
        Text("Instructions", style = MaterialTheme.typography.titleMedium)
        Text("Provide step-by-step instructions to make the recipe.", style = MaterialTheme.typography.bodySmall)
        recipe.instructions.forEachIndexed { index, instruction ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${index + 1}.", modifier = Modifier.width(32.dp))
                OutlinedTextField(
                    value = instruction,
                    onValueChange = { onUpdateInstruction(index, it) },
                    label = { Text("Step ${index + 1}") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onRemoveInstruction(index) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove Instruction")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        Button(onClick = { onAddInstruction("") }) {
            Icon(Icons.Outlined.Add, contentDescription = "Remove Instruction")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add Instruction")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Notes
        Text("Additional Notes", style = MaterialTheme.typography.titleMedium)
        Text("Add any extra notes or tips for the recipe.", style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(
            value = recipe.notes ?: "",
            onValueChange = { onRecipeFieldChange(RecipeViewModel.RecipeField.NOTES, it) },
            label = { Text("Notes") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Is Public
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Make Recipe Public")
            Spacer(modifier = Modifier.weight(1f))
            Switch(
                checked = recipe.isPublic,
                onCheckedChange = { onRecipeFieldChange(RecipeViewModel.RecipeField.IS_PUBLIC, it) }
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    // Dialogs
    if (showCategoryDialog) {
        CategorySelectionDialog(
            selectedCategories = recipe.categories,
            onCategoryToggle = { category ->
                val updatedCategories = if (category in recipe.categories) {
                    recipe.categories - category
                } else {
                    recipe.categories + category
                }
                onRecipeFieldChange(RecipeViewModel.RecipeField.CATEGORIES, updatedCategories)
            },
            onDismiss = { showCategoryDialog = false },
            onConfirm = { showCategoryDialog = false }
        )
    }

    if (showDietTypeDialog) {
        DietTypeSelectionDialog(
            selectedDietTypes = recipe.dietTypes,
            onDietTypeToggle = { dietType ->
                val updatedDietTypes = if (dietType in recipe.dietTypes) {
                    recipe.dietTypes - dietType
                } else {
                    recipe.dietTypes + dietType
                }
                onRecipeFieldChange(RecipeViewModel.RecipeField.DIET_TYPES, updatedDietTypes)
            },
            onDismiss = { showDietTypeDialog = false },
            onConfirm = { showDietTypeDialog = false }
        )
    }

    if (showToolsDialog) {
        TagSelectionDialog(
            title = "Select Needed Tools",
            tags = mapOf("Tools" to RecipeConstants.TOOLS),
            selectedTags = recipe.neededTools ?: emptyList(),
            onTagSelected = { tool ->
                onRecipeFieldChange(
                    RecipeViewModel.RecipeField.NEEDED_TOOLS,
                    (recipe.neededTools ?: emptyList()) + tool
                )
            },
            onTagDeselected = { tool ->
                onRecipeFieldChange(
                    RecipeViewModel.RecipeField.NEEDED_TOOLS,
                    (recipe.neededTools ?: emptyList()) - tool
                )
            },
            onDismiss = { showToolsDialog = false },
            onConfirm = { showToolsDialog = false }
        )
    }

    if (showComponentDialog) {
        val existingComponent = currentEditingComponentIndex?.let { recipe.components[it] }
        ComponentDialog(
            onDismiss = { showComponentDialog = false },
            onConfirm = { newComponent ->
                val updatedComponents = recipe.components.toMutableList()
                if (currentEditingComponentIndex != null) {
                    updatedComponents[currentEditingComponentIndex!!] = newComponent
                } else {
                    updatedComponents.add(newComponent)
                }
                onRecipeFieldChange(RecipeViewModel.RecipeField.COMPONENTS, updatedComponents)
            },
            existingComponent = existingComponent
        )
    }
}

@Composable
fun ComponentItem(
    component: Recipe.Component,
    onUpdate: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(component.title ?: "Untitled Component", style = MaterialTheme.typography.titleMedium)
            component.ingredients.forEach { ingredient ->
                Text("${ingredient.amount} ${ingredient.unit} ${ingredient.name}")
            }
            Row {
                Button(onClick = onUpdate) {
                    Text("Edit")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onRemove) {
                    Text("Remove")
                }
            }
        }
    }
}

@Composable
fun InstructionItem(
    instruction: String,
    onUpdate: (String) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(instruction, modifier = Modifier.weight(1f))
            IconButton(onClick = { /* Open edit dialog */ }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove")
            }
        }
    }
}