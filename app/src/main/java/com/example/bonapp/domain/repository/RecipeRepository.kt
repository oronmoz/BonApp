package com.example.bonapp.domain.repository

import android.net.Uri
import com.example.bonapp.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    val allRecipes: Flow<List<Recipe>>
    suspend fun getRecipe(id: String): Result<Recipe?>
    suspend fun addRecipe(recipe: Recipe): Result<Unit>
    suspend fun updateRecipe(recipe: Recipe): Result<Unit>
    suspend fun deleteRecipe(recipe: Recipe): Result<Unit>
    suspend fun syncWithFirestore(): Result<Unit>
    suspend fun uploadImage(uri: Uri): String
    suspend fun saveRecipe(recipe: Recipe): Result<Unit>
    suspend fun getAllRecipes(): List<Recipe>
    suspend fun searchRecipes(query: String): List<Recipe>
    suspend fun getForYouRecipes(page: Int, pageSize: Int): List<Recipe>
    suspend fun getFollowingRecipes(page: Int, pageSize: Int): List<Recipe>
    suspend fun getRecipesByAuthor(authorId: String): List<Recipe>
    suspend fun getSavedRecipes(userId: String): List<Recipe>
    suspend fun getPlannedRecipes(userId: String): List<Recipe>
    suspend fun searchRecipes(query: String, filters: Map<String, Any>): List<Recipe>
    suspend fun toggleFavorite(userId: String, recipeId: String): Result<Boolean>
    suspend fun togglePlanned(userId: String, recipeId: String): Result<Boolean>
    suspend fun getFavoriteRecipes(userId: String): List<Recipe>
    suspend fun addComment(recipeId: String, comment: Recipe.Comment): Result<Unit>
    suspend fun addReview(recipeId: String, review: Recipe.Review): Result<Unit>
    suspend fun searchRecipes(
        query: String,
        categories: List<String>,
        dietTypes: List<String>,
        minPrepTime: Int?,
        maxPrepTime: Int?,
        difficulties: List<String>
    ): List<Recipe>
}