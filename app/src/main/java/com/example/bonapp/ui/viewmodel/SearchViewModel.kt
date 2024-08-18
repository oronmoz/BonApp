package com.example.bonapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bonapp.domain.model.Recipe
import com.example.bonapp.domain.model.SearchFilters
import com.example.bonapp.domain.repository.AuthRepository
import com.example.bonapp.domain.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Recipe>>(emptyList())
    val searchResults: StateFlow<List<Recipe>> = _searchResults

    private val _searchFilters = MutableStateFlow(SearchFilters())
    val searchFilters: StateFlow<SearchFilters> = _searchFilters

    fun updateSearchFilters(newFilters: SearchFilters) {
        _searchFilters.value = newFilters
        search()
    }

    fun search() {
        viewModelScope.launch {
            try {
                val results = recipeRepository.searchRecipes(
                    query = _searchFilters.value.name,
                    categories = _searchFilters.value.categories,
                    dietTypes = _searchFilters.value.dietTypes,
                    minPrepTime = _searchFilters.value.minPrepTime,
                    maxPrepTime = _searchFilters.value.maxPrepTime,
                    difficulties = _searchFilters.value.difficulties
                )
                _searchResults.value = results
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun clearSearch() {
        _searchFilters.value = SearchFilters()
        _searchResults.value = emptyList()
    }

    fun getCurrentUserId(): String {
        return authRepository.currentUser?.uid ?: ""
    }
}