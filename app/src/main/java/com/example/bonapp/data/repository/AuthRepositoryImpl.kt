package com.example.bonapp.data.repository

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.bonapp.domain.model.User
import com.example.bonapp.domain.repository.AuthRepository
import com.google.api.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
        context: android.content.Context
) : AuthRepository {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("auth_prefs", MODE_PRIVATE)

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user!!
            // Save user email to SharedPreferences
            sharedPreferences.edit().putString("user_email", email).apply()
            // Create and return User object
            Result.success(User(id = firebaseUser.uid, email = firebaseUser.email ?: ""))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun register(user: User, password: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(user.email, password).await()
            val firebaseUser = result.user!!
            firebaseUser.updateProfile(com.google.firebase.auth.UserProfileChangeRequest.Builder().setDisplayName(user.name).build()).await()

            val userWithId = user.copy(id = firebaseUser.uid)
            firestore.collection("users").document(firebaseUser.uid).set(userWithId).await()

            Result.success(userWithId)
        } catch (e: Exception) {
            Timber.tag("AuthRepository").e(e, "Registration error")
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(): Result<User> {
        val uid = currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
        return try {
            val user = getUserProfile(uid)
            Result.success(user)
        } catch (e: Exception) {
            Timber.tag("AuthRepository").e(e, "Error fetching user profile")
            Result.failure(e)
        }
    }

    private suspend fun getUserProfile(uid: String): User {
        val snapshot = firestore.collection("users").document(uid).get().await()
        return snapshot.toObject(User::class.java) ?: throw Exception("User not found")
    }

    override suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            firestore.collection("users").document(user.id).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.tag("AuthRepository").e(e, "Error updating user profile")
            Result.failure(e)
        }
    }

    override suspend fun autoLogin(): Result<User>? {
        val savedEmail = sharedPreferences.getString("user_email", null)
        return if (savedEmail != null && currentUser != null) {
            Result.success(User(id = currentUser!!.uid, email = savedEmail))
        } else {
            null
        }
    }

    private suspend fun <T> retryFirestoreOperation(operation: suspend () -> T): T {
        val maxRetries = 3
        var lastException: Exception? = null

        for (attempt in 1..maxRetries) {
            try {
                return operation()
            } catch (e: Exception) {
                lastException = e
                delay(attempt * 1000L)
            }
        }
        throw lastException ?: IllegalStateException("Operation failed after $maxRetries attempts")
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
        // Clear SharedPreferences
        sharedPreferences.edit().remove("user_email").apply()
    }
}