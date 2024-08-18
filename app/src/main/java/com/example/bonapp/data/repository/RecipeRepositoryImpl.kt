package com.example.bonapp.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import com.example.bonapp.data.local.LocalStatus
import com.example.bonapp.data.local.RecipeDao
import com.example.bonapp.data.local.RecipeEntity
import com.example.bonapp.data.remote.FirestoreRepository
import com.example.bonapp.domain.model.Recipe
import com.example.bonapp.domain.repository.RecipeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class RecipeRepositoryImpl @Inject constructor(
    private val recipeDao: RecipeDao,
    private val firestoreRepository: FirestoreRepository,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    @ApplicationContext private val context: Context
) : RecipeRepository {

    override val allRecipes: Flow<List<Recipe>> = flow {
        if (isNetworkAvailable()) {
            val recipes = getAllRecipes()
            emit(recipes)
        } else {
            recipeDao.getUserUploadedRecipes().collect { entities ->
                emit(entities.map { it.toRecipe() })
            }
        }
    }

    override suspend fun getRecipe(id: String): Result<Recipe?> {
        return try {
            val recipe = if (isNetworkAvailable()) {
                firestoreRepository.getRecipe(id)
            } else {
                recipeDao.getRecipeById(id, LocalStatus.UPLOADED)?.toRecipe()
            }
            Result.success(recipe)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addRecipe(recipe: Recipe): Result<Unit> {
        return try {
            if (isNetworkAvailable()) {
                firestoreRepository.addRecipe(recipe)
            }
            recipeDao.insertRecipe(recipe.toEntity(LocalStatus.UPLOADED))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateRecipe(recipe: Recipe): Result<Unit> {
        return try {
            if (isNetworkAvailable()) {
                firestoreRepository.updateRecipe(recipe)
            }
            recipeDao.insertRecipe(recipe.toEntity(LocalStatus.UPLOADED))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveRecipe(recipe: Recipe): Result<Unit> {
        return try {
            if (isNetworkAvailable()) {
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")

                // Ensure the recipe has the correct author
                val recipeWithAuthor = recipe.copy(author = userId)

                // Add the recipe to the recipes collection
                firestore.collection("recipes").document(recipeWithAuthor.id).set(recipeWithAuthor).await()
            }

            // Save to local database
            recipeDao.insertRecipe(recipe.toEntity(LocalStatus.UPLOADED))

            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed to save recipe")
            Result.failure(e)
        }
    }

    override suspend fun deleteRecipe(recipe: Recipe): Result<Unit> {
        return try {
            recipeDao.deleteRecipe(recipe.toEntity(LocalStatus.UPLOADED))
            if (isNetworkAvailable()) {
                firestoreRepository.deleteRecipe(recipe.id)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchRecipes(query: String): List<Recipe> {
        return try {
            firestore.collection("recipes")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + '\uf8ff')
                .get()
                .await()
                .toObjects(Recipe::class.java)
        } catch (e: Exception) {
            Timber.e(e, "Error searching recipes")
            emptyList()
        }
    }

    override suspend fun getForYouRecipes(page: Int, pageSize: Int): List<Recipe> {
        return try {
            firestore.collection("recipes")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())
                .get()
                .await()
                .toObjects(Recipe::class.java)
        } catch (e: Exception) {
            Timber.e(e, "Error fetching 'For You' recipes")
            emptyList()
        }
    }

    override suspend fun getFollowingRecipes(page: Int, pageSize: Int): List<Recipe> {
        val currentUserId = auth.currentUser?.uid ?: return emptyList()
        return try {
            val followingIds = firestore.collection("users")
                .document(currentUserId)
                .collection("following")
                .get()
                .await()
                .documents
                .map { it.id }

            firestore.collection("recipes")
                .whereIn("authorId", followingIds)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())
                .get()
                .await()
                .toObjects(Recipe::class.java)
        } catch (e: Exception) {
            Timber.e(e, "Error fetching 'Following' recipes")
            emptyList()
        }
    }

    override suspend fun syncWithFirestore(): Result<Unit> {
        return try {
            if (isNetworkAvailable()) {
                val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
                val userDoc = firestore.collection("users").document(userId).get().await()
                val savedRecipeIds = userDoc.get("savedRecipes") as? List<String> ?: emptyList()
                val plannedRecipeIds = userDoc.get("plannedRecipes") as? List<String> ?: emptyList()

                val allRelevantIds = (savedRecipeIds + plannedRecipeIds).distinct()
                val relevantRecipes = firestoreRepository.getRecipesByIds(allRelevantIds)

                relevantRecipes.forEach { recipe ->
                    val status = when {
                        recipe.id in savedRecipeIds -> LocalStatus.SAVED
                        recipe.id in plannedRecipeIds -> LocalStatus.PLANNED
                        else -> LocalStatus.UPLOADED
                    }
                    recipeDao.insertRecipe(recipe.toEntity(status))
                }

                // Clean up local database
                withContext(Dispatchers.IO) {
                    recipeDao.getUserUploadedRecipes().collect { localRecipes ->
                        localRecipes.forEach { localRecipe ->
                            if (localRecipe.id !in allRelevantIds) {
                                recipeDao.deleteRecipe(localRecipe)
                            }
                        }
                    }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }

    private fun Recipe.toEntity(status: LocalStatus): RecipeEntity {
        return RecipeEntity(
            id = this.id,
            name = this.name,
            author = this.author,
            categories = this.categories.joinToString(","),
            dietType = this.dietTypes.joinToString(","),
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            prepTime = this.prepTime ?: 0,
            totalTime = this.totalTime,
            yields = this.yields,
            ingredients = this.components.flatMap { it.ingredients }.joinToString("|") { "${it.amount},${it.unit},${it.name}" },
            instructions = this.instructions.joinToString("|"),
            isPublic = this.isPublic,
            localStatus = status,
            neededTools = this.neededTools?.joinToString(",") ?: ""
        )
    }

    private fun RecipeEntity.toRecipe(): Recipe {
        return Recipe(
            id = this.id,
            name = this.name,
            author = this.author,
            categories = this.categories.split(","),
            dietTypes = this.dietType.split(","),
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            prepTime = this.prepTime,
            totalTime = this.totalTime,
            yields = this.yields,
            components = listOf(Recipe.Component(
                title = "Ingredients",
                ingredients = this.ingredients.split("|").map {
                    val (amount, unit, name) = it.split(",")
                    Recipe.Ingredient(amount, unit, name)
                }
            )),
            instructions = this.instructions.split("|"),
            isPublic = this.isPublic,
            neededTools = this.neededTools.split(",").filter { it.isNotEmpty() }
        )
    }


    override suspend fun getAllRecipes(): List<Recipe> {
        return firestoreRepository.getRecipes()
    }

    override suspend fun getRecipesByAuthor(authorId: String): List<Recipe> {
        return try {
            if (isNetworkAvailable()) {
                val snapshots = firestore.collection("recipes")
                    .whereEqualTo("author", authorId)
                    .get()
                    .await()
                snapshots.toObjects(Recipe::class.java)
            } else {
                // Fallback to local database if network is not available
                recipeDao.getRecipesByAuthor(authorId).map { it.toRecipe() }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching recipes by author")
            emptyList()
        }
    }

    override suspend fun uploadImage(uri: Uri): String {
        return try {
            val ref = storage.reference.child("recipe_images/${System.currentTimeMillis()}_${uri.lastPathSegment}")
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            Timber.e(e, "Error uploading image")
            throw e
        }
    }

    override suspend fun searchRecipes(query: String, filters: Map<String, Any>): List<Recipe> {
        return try {
            var queryRef = firestore.collection("recipes")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + '\uf8ff')

            filters.forEach { (key, value) ->
                when (key) {
                    "category" -> queryRef = queryRef.whereArrayContains("categories", value as String)
                    "dietType" -> queryRef = queryRef.whereEqualTo("dietType", value as String)
                    "prepTime" -> queryRef = queryRef.whereLessThanOrEqualTo("prepTime", value as Int)
                    // Add more filters as needed
                }
            }

            queryRef.get().await().toObjects(Recipe::class.java)
        } catch (e: Exception) {
            Timber.e(e, "Error searching recipes")
            emptyList()
        }
    }

    override suspend fun searchRecipes(
        query: String,
        categories: List<String>,
        dietTypes: List<String>,
        minPrepTime: Int?,
        maxPrepTime: Int?,
        difficulties: List<String>
    ): List<Recipe> {
        return try {
            var firestoreQuery = firestore.collection("recipes")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + '\uf8ff')

            // Apply category filter if specified
            if (categories.isNotEmpty()) {
                firestoreQuery = firestoreQuery.whereArrayContainsAny("categories", categories)
            }

            // Apply diet type filter if specified
            if (dietTypes.isNotEmpty()) {
                firestoreQuery = firestoreQuery.whereArrayContainsAny("dietTypes", dietTypes)
            }

            if (difficulties.isNotEmpty()) {
                firestoreQuery = firestoreQuery.whereArrayContainsAny("categories", difficulties)
            }

            // Fetch the results
            val querySnapshot = firestoreQuery.get().await()

            // Convert to Recipe objects and apply remaining filters in-memory
            querySnapshot.toObjects(Recipe::class.java)
                .filter { recipe ->
                    (minPrepTime == null || recipe.prepTime!! >= minPrepTime) &&
                            (maxPrepTime == null || recipe.prepTime!! <= maxPrepTime)
                }
        } catch (e: Exception) {
            Timber.e(e, "Error searching recipes")
            emptyList()
        }
    }

    override suspend fun toggleFavorite(userId: String, recipeId: String): Result<Boolean> {
        return try {
            val userRef = firestore.collection("users").document(userId)
            val userDoc = userRef.get().await()
            val favorites = userDoc.get("favorites") as? List<String> ?: emptyList()

            val updatedFavorites = if (recipeId in favorites) {
                favorites - recipeId
            } else {
                favorites + recipeId
            }

            userRef.update("favorites", updatedFavorites).await()
            Result.success(recipeId in updatedFavorites)
        } catch (e: Exception) {
            Timber.e(e, "Error toggling favorite")
            Result.failure(e)
        }
    }

    override suspend fun togglePlanned(userId: String, recipeId: String): Result<Boolean> {
        return try {
            val userRef = firestore.collection("users").document(userId)
            val userDoc = userRef.get().await()
            val planned = userDoc.get("plannedList") as? List<String> ?: emptyList()

            val updatedPlanned = if (recipeId in planned) {
                planned - recipeId
            } else {
                planned + recipeId
            }

            userRef.update("plannedList", updatedPlanned).await()
            Result.success(recipeId in updatedPlanned)
        } catch (e: Exception) {
            Timber.e(e, "Error toggling planned")
            Result.failure(e)
        }
    }

    override suspend fun getFavoriteRecipes(userId: String): List<Recipe> {
        return try {
            val userDoc = firestore.collection("users").document(userId).get().await()
            val favoriteIds = userDoc.get("favorites") as? List<String> ?: emptyList()
            if (favoriteIds.isNotEmpty()) {
                firestore.collection("recipes")
                    .whereIn("id", favoriteIds)
                    .get()
                    .await()
                    .toObjects(Recipe::class.java)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching favorite recipes")
            emptyList()
        }
    }


    override suspend fun getSavedRecipes(userId: String): List<Recipe> {
        return try {
            val userDoc = firestore.collection("users").document(userId).get().await()
            val savedRecipeIds = userDoc.get("savedRecipes") as? List<String> ?: emptyList()
            if (savedRecipeIds.isNotEmpty()) {
                firestore.collection("recipes")
                    .whereIn("id", savedRecipeIds)
                    .get()
                    .await()
                    .toObjects(Recipe::class.java)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching saved recipes")
            emptyList()
        }
    }

    override suspend fun getPlannedRecipes(userId: String): List<Recipe> {
        return try {
            val userDoc = firestore.collection("users").document(userId).get().await()
            val plannedRecipeIds = userDoc.get("plannedList") as? List<String> ?: emptyList()
            if (plannedRecipeIds.isNotEmpty()) {
                firestore.collection("recipes")
                    .whereIn("id", plannedRecipeIds)
                    .get()
                    .await()
                    .toObjects(Recipe::class.java)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching planned recipes")
            emptyList()
        }
    }

    override suspend fun addComment(recipeId: String, comment: Recipe.Comment): Result<Unit> {
        return try {
            val recipeRef = firestore.collection("recipes").document(recipeId)
            recipeRef.update("comments", FieldValue.arrayUnion(comment)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error adding comment")
            Result.failure(e)
        }
    }

    override suspend fun addReview(recipeId: String, review: Recipe.Review): Result<Unit> {
        return try {
            val recipeRef = firestore.collection("recipes").document(recipeId)
            recipeRef.update("reviews", FieldValue.arrayUnion(review)).await()

            // Update the rating
            val recipeSnapshot = recipeRef.get().await()
            val recipe = recipeSnapshot.toObject(Recipe::class.java)
            if (recipe != null) {
                val updatedRating = calculateNewRating(recipe.reviews, review)
                recipeRef.update("rating", updatedRating).await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error adding review")
            Result.failure(e)
        }
    }

    private fun calculateNewRating(currentReviews: List<Recipe.Review>, newReview: Recipe.Review): Double {
        val allReviews = currentReviews + newReview
        return allReviews.map { it.rating.toDouble() }.average()
    }

}