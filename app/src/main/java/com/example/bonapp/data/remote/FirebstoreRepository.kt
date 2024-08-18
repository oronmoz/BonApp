package com.example.bonapp.data.remote

import android.util.Log
import com.example.bonapp.domain.model.Recipe
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getRecipes(): List<Recipe> {
        return try {
            firestore.collection("recipes")
                .get()
                .await()
                .toObjects(Recipe::class.java)
        } catch (e: Exception) {
            Timber.tag("FirestoreRepository").e(e, "Error getting recipes")
            emptyList()
        }
    }

    suspend fun getRecipesByIds(ids: List<String>): List<Recipe> {
        return try {
            if (ids.isEmpty()) {
                return emptyList()
            }
            firestore.collection("recipes")
                .whereIn("id", ids)
                .get()
                .await()
                .toObjects(Recipe::class.java)
        } catch (e: Exception) {
            Timber.tag("FirestoreRepository").e(e, "Error getting recipes by IDs")
            emptyList()
        }
    }

    suspend fun getRecipe(id: String): Recipe? {
        return firestore.collection("recipes")
            .document(id)
            .get()
            .await()
            .toObject(Recipe::class.java)
    }

    suspend fun addRecipe(recipe: Recipe) {
        try {
            firestore.collection("recipes")
                .document(recipe.id)
                .set(recipe)
                .await()
        } catch (e: Exception) {
            Timber.tag("FirestoreRepository").e(e, "Error adding recipe")
            throw e
        }
    }

    suspend fun updateRecipe(recipe: Recipe) {
        firestore.collection("recipes")
            .document(recipe.id)
            .set(recipe)
            .await()
    }

    suspend fun deleteRecipe(id: String) {
        firestore.collection("recipes")
            .document(id)
            .delete()
            .await()
    }

    suspend fun getRecipesByCategory(category: String): List<Recipe> {
        return firestore.collection("recipes")
            .whereEqualTo("category", category)
            .get()
            .await()
            .toObjects(Recipe::class.java)
    }

    suspend fun getRecipesByDietType(dietType: String): List<Recipe> {
        return firestore.collection("recipes")
            .whereEqualTo("dietType", dietType)
            .get()
            .await()
            .toObjects(Recipe::class.java)
    }

    suspend fun getRecentPublicRecipes(limit: Int): List<Recipe> {
        return firestore.collection("recipes")
            .whereEqualTo("isPublic", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()
            .toObjects(Recipe::class.java)
    }

    suspend fun getRecipesByAuthor(authorId: String): List<Recipe> {
        return firestore.collection("recipes")
            .whereEqualTo("author", authorId)
            .get()
            .await()
            .toObjects(Recipe::class.java)
    }
}