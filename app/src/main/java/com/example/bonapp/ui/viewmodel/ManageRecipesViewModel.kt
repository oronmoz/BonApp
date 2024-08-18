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

// ManageRecipesViewModel.kt
@HiltViewModel
class ManageRecipesViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Recipe>>(emptyList())
    val favorites: StateFlow<List<Recipe>> = _favorites

    private val _planned = MutableStateFlow<List<Recipe>>(emptyList())
    val planned: StateFlow<List<Recipe>> = _planned

    init {
        loadFavorites()
        loadPlanned()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            val userId = authRepository.currentUser?.uid ?: return@launch
            _favorites.value = recipeRepository.getFavoriteRecipes(userId)
        }
    }

    private fun loadPlanned() {
        viewModelScope.launch {
            val userId = authRepository.currentUser?.uid ?: return@launch
            _planned.value = recipeRepository.getPlannedRecipes(userId)
        }
    }

    fun toggleFavorite(recipeId: String) {
        viewModelScope.launch {
            val userId = authRepository.currentUser?.uid ?: return@launch
            recipeRepository.toggleFavorite(userId, recipeId)
            loadFavorites()
        }
    }

    fun togglePlanned(recipeId: String) {
        viewModelScope.launch {
            val userId = authRepository.currentUser?.uid ?: return@launch
            recipeRepository.togglePlanned(userId, recipeId)
            loadPlanned()
        }
    }
}