package com.example.bonapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bonapp.domain.model.Recipe
import com.example.bonapp.domain.model.User
import com.example.bonapp.domain.repository.AuthRepository
import com.example.bonapp.domain.repository.RecipeRepository
import com.example.bonapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _myRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val myRecipes: StateFlow<List<Recipe>> = _myRecipes

    private val _savedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val savedRecipes: StateFlow<List<Recipe>> = _savedRecipes

    private val _plannedRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val plannedRecipes: StateFlow<List<Recipe>> = _plannedRecipes

    private val _currentUserId = MutableStateFlow("")
    val currentUserId: StateFlow<String> = _currentUserId

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        viewModelScope.launch {
            _currentUserId.value = authRepository.currentUser?.uid ?: ""
            refreshRecipes()
        }
    }

    private suspend fun loadUserAndRecipes() {
        try {
            val userResult = userRepository.getUserProfile()
            if (userResult.isSuccess) {
                val user = userResult.getOrNull()
                if (user != null) {
                    _currentUserId.value = user.id
                    loadAllRecipes()
                } else {
                    _error.value = "User not found"
                }
            } else {
                _error.value = "Failed to load user profile: ${userResult.exceptionOrNull()?.message}"
            }
        } catch (e: Exception) {
            _error.value = "Failed to load user profile: ${e.message}"
        }
    }

    private suspend fun loadAllRecipes() {
        loadMyRecipes()
        loadSavedRecipes()
        loadPlannedRecipes()
    }

    private suspend fun loadMyRecipes() {
        try {
            _myRecipes.value = recipeRepository.getRecipesByAuthor(_currentUserId.value)
        } catch (e: Exception) {
            _error.value = "Failed to load your recipes: ${e.message}"
        }
    }

    private suspend fun loadSavedRecipes() {
        try {
            _savedRecipes.value = recipeRepository.getSavedRecipes(_currentUserId.value)
        } catch (e: Exception) {
            _error.value = "Failed to load saved recipes: ${e.message}"
        }
    }

    private suspend fun loadPlannedRecipes() {
        try {
            _plannedRecipes.value = recipeRepository.getPlannedRecipes(_currentUserId.value)
        } catch (e: Exception) {
            _error.value = "Failed to load planned recipes: ${e.message}"
        }
    }

    fun toggleFavorite(recipeId: String) {
        viewModelScope.launch {
            try {
                val userId = _currentUserId.value
                if (userId.isNotBlank()) {
                    val result = recipeRepository.toggleFavorite(userId, recipeId)
                    result.onSuccess { isFavorite ->
                        Timber.d("Toggle favorite success. Recipe: $recipeId, isFavorite: $isFavorite")
                        refreshRecipes()
                    }.onFailure { e ->
                        Timber.e(e, "Error toggling favorite")
                        _error.value = "Failed to toggle favorite: ${e.message}"
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error toggling favorite")
                _error.value = "Failed to toggle favorite: ${e.message}"
            }
        }
    }

    fun togglePlanned(recipeId: String) {
        viewModelScope.launch {
            try {
                val userId = _currentUserId.value
                if (userId.isNotBlank()) {
                    val result = recipeRepository.togglePlanned(userId, recipeId)
                    result.onSuccess { isPlanned ->
                        Timber.d("Toggle planned success. Recipe: $recipeId, isPlanned: $isPlanned")
                        refreshRecipes()
                    }.onFailure { e ->
                        Timber.e(e, "Error toggling planned")
                        _error.value = "Failed to toggle planned: ${e.message}"
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error toggling planned")
                _error.value = "Failed to toggle planned: ${e.message}"
            }
        }
    }

    private suspend fun updateRecipeInLists(recipeId: String) {
        val updatedRecipe = recipeRepository.getRecipe(recipeId).getOrNull()
        if (updatedRecipe != null) {
            _myRecipes.value = _myRecipes.value.map { if (it.id == recipeId) updatedRecipe else it }
            _savedRecipes.value = _savedRecipes.value.map { if (it.id == recipeId) updatedRecipe else it }
            _plannedRecipes.value = _plannedRecipes.value.map { if (it.id == recipeId) updatedRecipe else it }
        }
    }

    fun refreshRecipes() {
        viewModelScope.launch {
            try {
                val userId = _currentUserId.value
                if (userId.isNotBlank()) {
                    _myRecipes.value = recipeRepository.getRecipesByAuthor(userId)
                    _savedRecipes.value = recipeRepository.getFavoriteRecipes(userId)
                    _plannedRecipes.value = recipeRepository.getPlannedRecipes(userId)
                    Timber.d("Recipes refreshed. My: ${_myRecipes.value.size}, Saved: ${_savedRecipes.value.size}, Planned: ${_plannedRecipes.value.size}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error refreshing recipes")
                _error.value = "Failed to refresh recipes: ${e.message}"
            }
        }
    }


    fun clearError() {
        _error.value = null
    }
}