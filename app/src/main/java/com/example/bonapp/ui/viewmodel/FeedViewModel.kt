package com.example.bonapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bonapp.domain.model.Recipe
import com.example.bonapp.domain.repository.AuthRepository
import com.example.bonapp.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class FeedType {
    FOR_YOU, FOLLOWING
}

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _filters = MutableStateFlow<Map<String, Any>>(emptyMap())
    val filters: StateFlow<Map<String, Any>> = _filters

    private val _searchResults = MutableStateFlow<List<Recipe>>(emptyList())
    val searchResults: StateFlow<List<Recipe>> = _searchResults

    private var currentPage = 0
    private val pageSize = 20
    private var currentFeedType = FeedType.FOR_YOU

    init {
        loadMoreRecipes()
    }

    fun getCurrentUserId(): String? {
        return authRepository.currentUser?.uid
    }

    fun loadMoreRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            val newRecipes = when (currentFeedType) {
                FeedType.FOR_YOU -> recipeRepository.getForYouRecipes(currentPage, pageSize)
                FeedType.FOLLOWING -> recipeRepository.getFollowingRecipes(currentPage, pageSize)
            }
            _recipes.value = _recipes.value.toMutableList().apply { addAll(newRecipes) }
            currentPage++
            _isLoading.value = false
        }
    }

    fun switchFeed(feedType: FeedType) {
        if (feedType != currentFeedType) {
            currentFeedType = feedType
            currentPage = 0
            _recipes.value = emptyList()
            loadMoreRecipes()
        }
    }

    fun refresh() {
        currentPage = 0
        _recipes.value = emptyList()
        loadMoreRecipes()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        searchRecipes()
    }

    fun updateFilters(newFilters: Map<String, Any>) {
        _filters.value = newFilters
        searchRecipes()
    }

    private fun searchRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            _searchResults.value = recipeRepository.searchRecipes(_searchQuery.value, _filters.value)
            _isLoading.value = false
        }
    }

    fun toggleFavorite(recipeId: String) {
        viewModelScope.launch {
            val userId = authRepository.currentUser?.uid ?: return@launch
            recipeRepository.toggleFavorite(userId, recipeId)
        }
    }

    fun togglePlanned(recipeId: String) {
        viewModelScope.launch {
            val userId = authRepository.currentUser?.uid ?: return@launch
            recipeRepository.togglePlanned(userId, recipeId)
        }
    }

}