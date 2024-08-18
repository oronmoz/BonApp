package com.example.bonapp.domain.repository

import com.example.bonapp.domain.model.User
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(user: User, password: String): Result<User>
    suspend fun getUserProfile(): Result<User>
    suspend fun updateUserProfile(user: User): Result<Unit>
    suspend fun logout()
    suspend fun autoLogin(): Result<User>?
}