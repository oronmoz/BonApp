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
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    private val _userRecipes = MutableStateFlow<List<Recipe>>(emptyList())
    val userRecipes: StateFlow<List<Recipe>> = _userRecipes

    private val _isCurrentUser = MutableStateFlow(false)
    val isCurrentUser: StateFlow<Boolean> = _isCurrentUser

    private val _isFollowing = MutableStateFlow(false)
    val isFollowing: StateFlow<Boolean> = _isFollowing

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            try {
                Timber.d("Loading user profile for userId: $userId")
                val user = userRepository.getUserProfile(userId)
                _userProfile.value = user
                checkIfFollowing(userId)
                Timber.d("User profile loaded: $user")
            } catch (e: Exception) {
                Timber.e(e, "Error loading user profile")
                _error.value = "Failed to load user profile: ${e.message}"
            }
        }
    }

    fun loadUserRecipes(userId: String) {
        viewModelScope.launch {
            try {
                Timber.d("Loading recipes for userId: $userId")
                val recipes = recipeRepository.getRecipesByAuthor(userId)
                _userRecipes.value = recipes
                Timber.d("User recipes loaded: ${recipes.size}")
            } catch (e: Exception) {
                Timber.e(e, "Error loading user recipes")
                _error.value = "Failed to load user recipes: ${e.message}"
            }
        }
    }

    fun checkIfCurrentUser(userId: String) {
        viewModelScope.launch {
            val currentUserId = authRepository.currentUser?.uid
            _isCurrentUser.value = currentUserId == userId
            Timber.d("Is current user: ${_isCurrentUser.value}")
        }
    }

    fun checkIfFollowing(userId: String) {
        viewModelScope.launch {
            try {
                Timber.d("Checking if following userId: $userId")
                val result = userRepository.isFollowing(userId)
                result.onSuccess {
                    _isFollowing.value = it
                    Timber.d("Is following: ${_isFollowing.value}")
                }.onFailure { e ->
                    Timber.e(e, "Error checking if following")
                    _error.value = "Failed to check follow status: ${e.message}"
                }
            } catch (e: Exception) {
                Timber.e(e, "Error checking if following")
                _error.value = "Failed to check follow status: ${e.message}"
            }
        }
    }

    fun toggleFollow(userId: String) {
        viewModelScope.launch {
            Timber.d("Attempting to toggle follow for user: $userId")
            try {
                val currentFollowState = _isFollowing.value
                Timber.d("Current follow state: $currentFollowState")

                val result = if (currentFollowState) {
                    Timber.d("Attempting to unfollow user")
                    userRepository.unfollowUser(userId)
                } else {
                    Timber.d("Attempting to follow user")
                    userRepository.followUser(userId)
                }

                result.onSuccess {
                    _isFollowing.value = !currentFollowState
                    Timber.d("Follow state toggled. New state: ${_isFollowing.value}")
                    loadUserProfile(userId)
                }.onFailure { error ->
                    Timber.e(error, "Failed to toggle follow")
                    _error.value = "Failed to toggle follow: ${error.message}"
                }
            } catch (e: Exception) {
                Timber.e(e, "Error toggling follow")
                _error.value = "Error toggling follow: ${e.message}"
            }
        }
    }


    fun getCurrentUserId(): String {
        return authRepository.currentUser?.uid ?: ""
    }

    fun clearError() {
        _error.value = null
    }


}