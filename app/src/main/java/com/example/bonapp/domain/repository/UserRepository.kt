package com.example.bonapp.domain.repository

import android.net.Uri
import com.example.bonapp.domain.model.User


interface UserRepository {
    suspend fun getUserProfile(): Result<User>
    suspend fun updateUserProfile(user: User): Result<Unit>
    suspend fun uploadProfileImage(uri: Uri): Result<String>
    suspend fun getUserProfile(userId: String): User
    suspend fun followUser(userId: String): Result<Unit>
    suspend fun unfollowUser(userId: String): Result<Unit>
    suspend fun isFollowing(userId: String): Result<Boolean>
}
