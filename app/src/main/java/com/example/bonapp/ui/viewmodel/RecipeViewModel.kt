package com.example.bonapp.ui.viewmodel

import android.net.Uri
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val repository: RecipeRepository,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _recipeState = MutableStateFlow<RecipeState>(RecipeState.Initial)
    val recipeState: StateFlow<RecipeState> = _recipeState

    private val _currentRecipe = MutableStateFlow<Recipe?>(null)
    val currentRecipe: StateFlow<Recipe?> = _currentRecipe

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private val _isPlanned = MutableStateFlow(false)
    val isPlanned: StateFlow<Boolean> = _isPlanned

    private val _isImageUploading = MutableStateFlow(false)
    val isImageUploading: StateFlow<Boolean> = _isImageUploading.asStateFlow()

    private val _imageUploadError = MutableStateFlow<String?>(null)
    val imageUploadError: StateFlow<String?> = _imageUploadError.asStateFlow()

    private val _author = MutableStateFlow<User?>(null)
    val author: StateFlow<User?> = _author.asStateFlow()

    private val _hasUnsavedChanges = MutableStateFlow(false)
    val hasUnsavedChanges: StateFlow<Boolean> = _hasUnsavedChanges


    private val _validationErrors = MutableStateFlow<List<String>>(emptyList())
    val validationErrors: StateFlow<List<String>> = _validationErrors

    fun validateRecipe(recipe: Recipe): List<String> {
        val errors = mutableListOf<String>()

        if (recipe.name.isBlank()) errors.add("Recipe name is required")
        if (recipe.categories.isEmpty()) errors.add("At least one category is required")
        if (recipe.dietTypes.isEmpty()) errors.add("At least one diet type is required")
        if (recipe.totalTime <= 0) errors.add("Total time must be set")
        if (recipe.yields.isBlank()) errors.add("Yields must be set")
        if (recipe.components.isEmpty()) errors.add("At least one component is required")
        if (recipe.instructions.isEmpty()) errors.add("At least one instruction is required")

        return errors
    }

    fun loadRecipe(id: String) {
        viewModelScope.launch {
            _recipeState.value = RecipeState.Loading
            try {
                val recipeResult = repository.getRecipe(id)
                if (recipeResult.isSuccess) {
                    val recipe = recipeResult.getOrNull()
                    _currentRecipe.value = recipe
                    _recipeState.value = RecipeState.Success(recipe!!)

                    val userId = authRepository.currentUser?.uid
                    if (userId != null) {
                        _isFavorite.value = repository.getFavoriteRecipes(userId).any { it.id == id }
                        _isPlanned.value = repository.getPlannedRecipes(userId).any { it.id == id } }
                    _currentRecipe.value?.let { recipe ->
                            _author.value = userRepository.getUserProfile(recipe.author)
                    }

                } else {
                    _recipeState.value = RecipeState.Error("Failed to load recipe")
                }
            } catch (e: Exception) {
                _recipeState.value = RecipeState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createNewRecipe() {
        _currentRecipe.value = Recipe(
            id = UUID.randomUUID().toString(),
            name = "",
            categories = emptyList(),
            dietTypes = emptyList(),
            author = "",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            sponsorship = null,
            pictureUrls = emptyList(),
            videoLink = null,
            about = null,
            inspiredByLinks = emptyList(),
            rating = 0.0,
            prepTime = null,
            totalTime = 0,
            yields = "",
            caloriesPerServing = null,
            neededTools = emptyList(),
            components = emptyList(),
            instructions = emptyList(),
            notes = null,
            reviews = emptyList(),
            isPublic = false
        )
    }

    fun updateRecipeField(field: RecipeField, value: Any) {
        _currentRecipe.value = _currentRecipe.value?.let { recipe ->
            when (field) {
                RecipeField.NAME -> recipe.copy(name = value as String)
                RecipeField.CATEGORIES -> recipe.copy(categories = value as List<String>)
                RecipeField.DIET_TYPES -> recipe.copy(dietTypes = value as List<String>)
                RecipeField.SPONSORSHIP -> recipe.copy(sponsorship = value as String?)
                RecipeField.VIDEO_LINK -> recipe.copy(videoLink = value as String?)
                RecipeField.ABOUT -> recipe.copy(about = value as String?)
                RecipeField.INSPIRED_BY_LINKS -> recipe.copy(inspiredByLinks = value as List<String>)
                RecipeField.PREP_TIME -> recipe.copy(prepTime = value as Int?)
                RecipeField.TOTAL_TIME -> recipe.copy(totalTime = value as Int)
                RecipeField.YIELDS -> recipe.copy(yields = value as String)
                RecipeField.CALORIES_PER_SERVING -> recipe.copy(caloriesPerServing = value as Int?)
                RecipeField.NEEDED_TOOLS -> recipe.copy(neededTools = value as List<String>)
                RecipeField.COMPONENTS -> recipe.copy(components = value as List<Recipe.Component>)
                RecipeField.INSTRUCTIONS -> recipe.copy(instructions = value as List<String>)
                RecipeField.NOTES -> recipe.copy(notes = value as String?)
                RecipeField.IS_PUBLIC -> recipe.copy(isPublic = value as Boolean)
            }
        }
        _hasUnsavedChanges.value = true
    }

    fun addComponent(component: Recipe.Component) {
        _currentRecipe.value = _currentRecipe.value?.let { recipe ->
            recipe.copy(components = recipe.components + component)
        }
    }

    fun updateComponent(index: Int, component: Recipe.Component) {
        _currentRecipe.value = _currentRecipe.value?.let { recipe ->
            val updatedComponents = recipe.components.toMutableList()
            updatedComponents[index] = component
            recipe.copy(components = updatedComponents)
        }
    }


    fun removeComponent(index: Int) {
        _currentRecipe.value = _currentRecipe.value?.let { recipe ->
            val updatedComponents = recipe.components.toMutableList()
            updatedComponents.removeAt(index)
            recipe.copy(components = updatedComponents)
        }
    }

    fun addInstruction(instruction: String) {
        _currentRecipe.value = _currentRecipe.value?.let { recipe ->
            recipe.copy(instructions = recipe.instructions + instruction)
        }
    }

    fun updateInstruction(index: Int, instruction: String) {
        _currentRecipe.value = _currentRecipe.value?.let { recipe ->
            val updatedInstructions = recipe.instructions.toMutableList()
            updatedInstructions[index] = instruction
            recipe.copy(instructions = updatedInstructions)
        }
    }

    fun removeInstruction(index: Int) {
        _currentRecipe.value = _currentRecipe.value?.let { recipe ->
            val updatedInstructions = recipe.instructions.toMutableList()
            updatedInstructions.removeAt(index)
            recipe.copy(instructions = updatedInstructions)
        }
    }

    fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            _isImageUploading.value = true
            _imageUploadError.value = null
            try {
                val url = repository.uploadImage(uri)
                _currentRecipe.value = _currentRecipe.value?.let { recipe ->
                    recipe.copy(pictureUrls = recipe.pictureUrls + url)
                }
            } catch (e: Exception) {
                _imageUploadError.value = e.message ?: "Failed to upload image"
            } finally {
                _isImageUploading.value = false
            }
        }
    }

    fun removeImage(uri: Uri) {
        _currentRecipe.value = _currentRecipe.value?.let { recipe ->
            recipe.copy(pictureUrls = recipe.pictureUrls.filter { it != uri.toString() })
        }
    }

    fun saveRecipe() {
        viewModelScope.launch {
            _recipeState.value = RecipeState.Loading
            val currentRecipe = _currentRecipe.value ?: return@launch
            val errors = validateRecipe(currentRecipe)
            if (errors.isNotEmpty()) {
                _validationErrors.value = errors
                _recipeState.value = RecipeState.Error("Please fix the following errors")
                return@launch
            }
            try {
                val result = repository.saveRecipe(currentRecipe)
                if (result.isSuccess) {
                    _recipeState.value = RecipeState.Success(currentRecipe)
                } else {
                    _recipeState.value = RecipeState.Error("Failed to save recipe: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                _recipeState.value = RecipeState.Error("An unexpected error occurred: ${e.message}")
            }
        }
        _hasUnsavedChanges.value = false
    }

    fun resetUnsavedChanges() {
        _hasUnsavedChanges.value = false
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            _recipeState.value = RecipeState.Loading
            try {
                val result = repository.deleteRecipe(recipe)
                if (result.isSuccess) {
                    _recipeState.value = RecipeState.Success(recipe)
                    _currentRecipe.value = null
                } else {
                    _recipeState.value = RecipeState.Error("Failed to delete recipe: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                _recipeState.value = RecipeState.Error("An unexpected error occurred: ${e.message}")
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val userId = authRepository.currentUser?.uid ?: return@launch
            val recipeId = _currentRecipe.value?.id ?: return@launch
            val result = repository.toggleFavorite(userId, recipeId)
            if (result.isSuccess) {
                _isFavorite.value = result.getOrNull() ?: false
                // Update the current recipe to reflect the change
                _currentRecipe.value = _currentRecipe.value?.copy(isFavorite = _isFavorite.value)
            }
        }
    }

    fun togglePlanned() {
        viewModelScope.launch {
            val userId = authRepository.currentUser?.uid ?: return@launch
            val recipeId = _currentRecipe.value?.id ?: return@launch
            val result = repository.togglePlanned(userId, recipeId)
            if (result.isSuccess) {
                _isPlanned.value = result.getOrNull() ?: false
            }
        }
    }

    fun addComment(comment: String) {
        viewModelScope.launch {
            val userId = authRepository.currentUser?.uid ?: return@launch
            val recipeId = _currentRecipe.value?.id ?: return@launch
            val newComment = Recipe.Comment(
                userId = userId,
                comment = comment,
                timestamp = System.currentTimeMillis()
            )
            val result = repository.addComment(recipeId, newComment)
            if (result.isSuccess) {
                _currentRecipe.value = _currentRecipe.value?.copy(
                    comments = (_currentRecipe.value?.comments ?: emptyList()) + newComment
                )
            } else {
                _recipeState.value = RecipeState.Error("Failed to add comment")
            }
        }
    }

    fun addReview(rating: Int, review: String) {
        viewModelScope.launch {
            val userId = authRepository.currentUser?.uid ?: return@launch
            val recipeId = _currentRecipe.value?.id ?: return@launch
            val newReview = Recipe.Review(
                userId = userId,
                rating = rating,
                comment = review
            )
            val result = repository.addReview(recipeId, newReview)
            if (result.isSuccess) {
                _currentRecipe.value = _currentRecipe.value?.copy(
                    reviews = (_currentRecipe.value?.reviews ?: emptyList()) + newReview
                )
            } else {
                _recipeState.value = RecipeState.Error("Failed to add review")
            }
        }
    }

    private fun calculateNewRating(currentReviews: List<Recipe.Review>?, newReview: Recipe.Review): Double {
        val allReviews = (currentReviews ?: emptyList()) + newReview
        return allReviews.map { it.rating }.average()
    }

    fun getCurrentUserId(): String {
        return authRepository.currentUser?.uid ?: ""
    }

    sealed class RecipeState {
        object Initial : RecipeState()
        object Loading : RecipeState()
        data class Success(val recipe: Recipe) : RecipeState()
        data class Error(val message: String) : RecipeState()
    }

    enum class RecipeField {
        NAME, CATEGORIES, DIET_TYPES, SPONSORSHIP, VIDEO_LINK, ABOUT, INSPIRED_BY_LINKS,
        PREP_TIME, TOTAL_TIME, YIELDS, CALORIES_PER_SERVING, NEEDED_TOOLS, COMPONENTS, INSTRUCTIONS, NOTES, IS_PUBLIC
    }
}