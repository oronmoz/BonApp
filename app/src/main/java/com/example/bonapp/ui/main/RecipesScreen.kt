package com.example.bonapp.ui.main
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bonapp.domain.model.Recipe
import com.example.bonapp.ui.components.forms.RecipeCard
import com.example.bonapp.ui.viewmodel.RecipesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    onNavigateToRecipeDetail: (String) -> Unit,
    onNavigateToEditRecipe: (String) -> Unit,
    viewModel: RecipesViewModel = hiltViewModel()
) {
    val myRecipes by viewModel.myRecipes.collectAsState()
    val savedRecipes by viewModel.savedRecipes.collectAsState()
    val plannedRecipes by viewModel.plannedRecipes.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("My Recipes", "Favorites", "Planned")

    LaunchedEffect(Unit) {
        viewModel.refreshRecipes()
    }

    LaunchedEffect(error) {
        error?.let {
            // Show error message, e.g., using a Snackbar
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipes") },
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }

            when (selectedTab) {
                0 -> RecipeList(
                    recipes = myRecipes,
                    currentUserId = currentUserId,
                    onRecipeClick = onNavigateToRecipeDetail,
                    onEditRecipe = onNavigateToEditRecipe,
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onTogglePlanned = { viewModel.togglePlanned(it) }
                )
                1 -> RecipeList(
                    recipes = savedRecipes,
                    currentUserId = currentUserId,
                    onRecipeClick = onNavigateToRecipeDetail,
                    onEditRecipe = onNavigateToEditRecipe,
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onTogglePlanned = { viewModel.togglePlanned(it) }
                )
                2 -> RecipeList(
                    recipes = plannedRecipes,
                    currentUserId = currentUserId,
                    onRecipeClick = onNavigateToRecipeDetail,
                    onEditRecipe = onNavigateToEditRecipe,
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onTogglePlanned = { viewModel.togglePlanned(it) }
                )
            }
        }
    }
}

@Composable
fun RecipeList(
    recipes: List<Recipe>,
    currentUserId: String,
    onRecipeClick: (String) -> Unit,
    onEditRecipe: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onTogglePlanned: (String) -> Unit
) {
    LazyColumn {
        items(recipes) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = { onRecipeClick(recipe.id) },
                onFavoriteClick = { onToggleFavorite(recipe.id) },
                onPlannedClick = { onTogglePlanned(recipe.id) },
                onEditClick = if (recipe.author == currentUserId) {
                    { onEditRecipe(recipe.id) }
                } else null,
                currentUserId = currentUserId // Pass the currentUserId here
            )
        }
    }
}