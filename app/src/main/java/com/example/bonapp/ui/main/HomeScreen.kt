package com.example.bonapp.ui.main

import com.example.bonapp.ui.components.dialogs.FilterDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bonapp.domain.model.SearchFilters
import com.example.bonapp.ui.components.forms.RecipeCard
import com.example.bonapp.ui.viewmodel.FeedViewModel
import com.example.bonapp.ui.viewmodel.FeedType
import com.example.bonapp.ui.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddRecipe: () -> Unit,
    onNavigateToRecipeDetail: (String) -> Unit,
    feedViewModel: FeedViewModel = hiltViewModel(),
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val listState = rememberLazyListState()
    val filters by searchViewModel.searchFilters.collectAsState()
    val searchResults by searchViewModel.searchResults.collectAsState()
    val recipes by feedViewModel.recipes.collectAsState()
    val isLoading by feedViewModel.isLoading.collectAsState()
    var showFilters by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    var isSearchMode by remember { mutableStateOf(false) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                if (lastIndex != null && lastIndex >= recipes.size - 1 && !isLoading) {
                    feedViewModel.loadMoreRecipes()
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BonApp") },
                actions = {
                    IconButton(onClick = { showFilters = true }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    if (isSearchMode) {
                        IconButton(onClick = {
                            isSearchMode = false
                            searchViewModel.clearSearch()
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Search")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToAddRecipe,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add Recipe") },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            if (!isSearchMode) {
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            feedViewModel.switchFeed(FeedType.FOR_YOU)
                        },
                        text = { Text("For You") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            feedViewModel.switchFeed(FeedType.FOLLOWING)
                        },
                        text = { Text("Following") }
                    )
                }
            }

            LazyColumn(state = listState) {
                val displayedRecipes = if (isSearchMode) searchResults else recipes
                if (displayedRecipes.isEmpty()) {
                    item {
                        Text(
                            text = if (isSearchMode) "No recipes found" else "No recipes available",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                } else {
                    items(displayedRecipes) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = { onNavigateToRecipeDetail(recipe.id) },
                            onFavoriteClick = { feedViewModel.toggleFavorite(recipe.id) },
                            onPlannedClick = { feedViewModel.togglePlanned(recipe.id) },
                            onEditClick = null,
                            currentUserId = feedViewModel.getCurrentUserId() ?: ""
                        )
                    }
                }
                item {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }

    if (showFilters) {
        FilterDialog(
            currentFilters = filters,
            onApplyFilters = { newFilters ->
                searchViewModel.updateSearchFilters(newFilters)
                searchViewModel.search()
                isSearchMode = true
                showFilters = false
            },
            onDismiss = {
                showFilters = false
            }
        )
    }
}