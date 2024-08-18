package com.example.bonapp.ui.main
import TagSelectionDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bonapp.ui.components.buttons.ReviewCard
import com.example.bonapp.ui.components.dialogs.CategorySelectionDialog
import com.example.bonapp.ui.components.dialogs.DietTypeSelectionDialog
import com.example.bonapp.ui.components.forms.RecipeCard
import com.example.bonapp.ui.viewmodel.SearchViewModel
import com.example.bonapp.util.RecipeConstants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    initialQuery: String,
    onNavigateToRecipe: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val searchFilters by viewModel.searchFilters.collectAsState()

    var showCategoryDialog by remember { mutableStateOf(false) }
    var showDietTypeDialog by remember { mutableStateOf(false) }
    var showDifficultyDialog by remember { mutableStateOf(false) }

    LaunchedEffect(initialQuery) {
        viewModel.updateSearchFilters(searchFilters.copy(name = initialQuery))
        viewModel.search()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Recipes") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchFilters.name,
                onValueChange = {
                    viewModel.updateSearchFilters(searchFilters.copy(name = it))
                    viewModel.search() // Trigger search on each text change
                },
                label = { Text("Search by name") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { showCategoryDialog = true }) {
                    Text("Categories (${searchFilters.categories.size})")
                }
                Button(onClick = { showDietTypeDialog = true }) {
                    Text("Diet Types (${searchFilters.dietTypes.size})")
                }
                Button(onClick = { showDifficultyDialog = true }) {
                    Text("Difficulty (${searchFilters.difficulties.size})")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = searchFilters.minPrepTime?.toString() ?: "",
                    onValueChange = { viewModel.updateSearchFilters(searchFilters.copy(minPrepTime = it.toIntOrNull())) },
                    label = { Text("Min Prep Time") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = searchFilters.maxPrepTime?.toString() ?: "",
                    onValueChange = { viewModel.updateSearchFilters(searchFilters.copy(maxPrepTime = it.toIntOrNull())) },
                    label = { Text("Max Prep Time") },
                    modifier = Modifier.weight(1f)
                )
            }
            if (searchResults.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No recipes found. Try adjusting your search criteria.")
                }
            } else {
                LazyColumn {
                    items(searchResults) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onNavigateToRecipe(recipe.id) },
                            onFavoriteClick = { /* Handle favorite click */ },
                            onPlannedClick = { /* Handle planned click */ },
                            onEditClick = null,
                            currentUserId = viewModel.getCurrentUserId()
                        )
                    }
                }
            }
        }
    }

    if (showCategoryDialog) {
        CategorySelectionDialog(
            selectedCategories = searchFilters.categories,
            onCategoryToggle = { category ->
                val updatedCategories = if (category in searchFilters.categories) {
                    searchFilters.categories - category
                } else {
                    searchFilters.categories + category
                }
                viewModel.updateSearchFilters(searchFilters.copy(categories = updatedCategories))
            },
            onDismiss = { showCategoryDialog = false },
            onConfirm = { showCategoryDialog = false }
        )
    }

    if (showDietTypeDialog) {
        DietTypeSelectionDialog(
            selectedDietTypes = searchFilters.dietTypes,
            onDietTypeToggle = { dietType ->
                val updatedDietTypes = if (dietType in searchFilters.dietTypes) {
                    searchFilters.dietTypes - dietType
                } else {
                    searchFilters.dietTypes + dietType
                }
                viewModel.updateSearchFilters(searchFilters.copy(dietTypes = updatedDietTypes))
            },
            onDismiss = { showDietTypeDialog = false },
            onConfirm = { showDietTypeDialog = false }
        )
    }

    if (showDifficultyDialog) {
        TagSelectionDialog(
            title = "Select Difficulty",
            tags = mapOf("Difficulty" to RecipeConstants.CATEGORIES["Difficulty"]!!),
            selectedTags = searchFilters.difficulties,
            onTagSelected = { difficulty ->
                val updatedDifficulties = searchFilters.difficulties + difficulty
                viewModel.updateSearchFilters(searchFilters.copy(difficulties = updatedDifficulties))
            },
            onTagDeselected = { difficulty ->
                val updatedDifficulties = searchFilters.difficulties - difficulty
                viewModel.updateSearchFilters(searchFilters.copy(difficulties = updatedDifficulties))
            },
            onDismiss = { showDifficultyDialog = false },
            onConfirm = { showDifficultyDialog = false }
        )
    }
}