package com.example.bonapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    // User-uploaded recipes
    @Query("SELECT * FROM recipes WHERE localStatus = :status")
    fun getUserUploadedRecipes(status: LocalStatus = LocalStatus.UPLOADED): Flow<List<RecipeEntity>>

    @Query("SELECT * FROM recipes WHERE id = :id AND localStatus = :status")
    suspend fun getRecipeById(id: String, status: LocalStatus): RecipeEntity?

    @Query("SELECT * FROM recipes WHERE author = :authorId AND localStatus = :status")
    suspend fun getRecipesByAuthor(authorId: String, status: LocalStatus = LocalStatus.UPLOADED): List<RecipeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)

    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)

    // User methods
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE users SET favorites = :savedRecipes WHERE id = :userId")
    suspend fun updateUserSavedRecipes(userId: String, savedRecipes: List<String>)

    @Query("UPDATE users SET plannedRecipes = :plannedRecipes WHERE id = :userId")
    suspend fun updateUserPlannedRecipes(userId: String, plannedRecipes: List<String>)

    // Methods to get saved and planned recipes
    @Query("SELECT * FROM recipes WHERE id IN (SELECT favorites FROM users WHERE id = :userId) AND localStatus = :status")
    suspend fun getSavedRecipes(userId: String, status: LocalStatus = LocalStatus.SAVED): List<RecipeEntity>

    @Query("SELECT * FROM recipes WHERE id IN (SELECT plannedRecipes FROM users WHERE id = :userId) AND localStatus = :status")
    suspend fun getPlannedRecipes(userId: String, status: LocalStatus = LocalStatus.PLANNED): List<RecipeEntity>

    // New method to update recipe status
    @Query("UPDATE recipes SET localStatus = :status WHERE id = :recipeId")
    suspend fun updateRecipeStatus(recipeId: String, status: LocalStatus)
}