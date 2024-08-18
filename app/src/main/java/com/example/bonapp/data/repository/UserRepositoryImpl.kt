package com.example.bonapp.data.repository

import android.net.Uri
import com.example.bonapp.domain.model.User
import com.example.bonapp.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : UserRepository {

    override suspend fun getUserProfile(): Result<User> {
        val uid = firebaseAuth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
        return try {
            val snapshot = firestore.collection("users").document(uid).get().await()
            val user = snapshot.toObject(User::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching user profile")
            Result.failure(e)
        }
    }

    override suspend fun getUserProfile(userId: String): User {
        return try {
            val snapshot = firestore.collection("users").document(userId).get().await()
            snapshot.toObject(User::class.java) ?: throw Exception("User not found")
        } catch (e: Exception) {
            Timber.e(e, "Error fetching user profile")
            throw e
        }
    }

    override suspend fun updateUserProfile(user: User): Result<Unit> {
        return try {
            firestore.collection("users").document(user.id).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error updating user profile")
            Result.failure(e)
        }
    }

    override suspend fun uploadProfileImage(uri: Uri): Result<String> {
        val user = firebaseAuth.currentUser ?: return Result.failure(Exception("User not logged in"))
        return try {
            val imageRef = storage.reference.child("profile_images/${user.uid}")
            val uploadTask = imageRef.putFile(uri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()

            // Update user profile with new image URL
            firestore.collection("users").document(user.uid)
                .update("profileImageUrl", downloadUrl).await()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Timber.e(e, "Error uploading profile image")
            Result.failure(e)
        }
    }

    override suspend fun followUser(userId: String): Result<Unit> {
        val currentUserId = firebaseAuth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
        return try {
            firestore.runTransaction { transaction ->
                val currentUserFollowingRef = firestore.collection("users").document(currentUserId)
                    .collection("following").document(userId)
                val targetUserFollowersRef = firestore.collection("users").document(userId)
                    .collection("followers").document(currentUserId)

                // Add to current user's following
                transaction.set(currentUserFollowingRef, mapOf("timestamp" to com.google.firebase.Timestamp.now()), SetOptions.merge())

                // Add to target user's followers
                transaction.set(targetUserFollowersRef, mapOf("timestamp" to com.google.firebase.Timestamp.now()), SetOptions.merge())
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error following user")
            Result.failure(e)
        }
    }

    override suspend fun unfollowUser(userId: String): Result<Unit> {
        val currentUserId = firebaseAuth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
        return try {
            firestore.runTransaction { transaction ->
                val currentUserFollowingRef = firestore.collection("users").document(currentUserId)
                    .collection("following").document(userId)
                val targetUserFollowersRef = firestore.collection("users").document(userId)
                    .collection("followers").document(currentUserId)

                // Remove from current user's following
                transaction.delete(currentUserFollowingRef)

                // Remove from target user's followers
                transaction.delete(targetUserFollowersRef)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error unfollowing user")
            Result.failure(e)
        }
    }


    override suspend fun isFollowing(userId: String): Result<Boolean> {
        val currentUserId = firebaseAuth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
        return try {
            val followingDoc = firestore.collection("users").document(currentUserId)
                .collection("following").document(userId)
                .get()
                .await()
            Result.success(followingDoc.exists())
        } catch (e: Exception) {
            Timber.e(e, "Error checking if following")
            Result.failure(e)
        }
    }

}
