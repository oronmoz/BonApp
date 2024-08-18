package com.example.bonapp.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bonapp.domain.model.User
import com.example.bonapp.domain.repository.AuthRepository
import com.example.bonapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isFollowing = MutableStateFlow(false)
    val isFollowing: StateFlow<Boolean> = _isFollowing.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            userRepository.getUserProfile()
                .onSuccess { _user.value = it }
                .onFailure { _error.value = it.message ?: "Failed to load user profile" }
            _isLoading.value = false
        }
    }

    fun uploadProfileImage(uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = userRepository.uploadProfileImage(uri)
                result.onSuccess { imageUrl ->
                    _user.value = _user.value?.copy(profileImageUrl = imageUrl)
                }.onFailure {
                    _error.value = it.message ?: "Failed to upload profile image"
                }
            } catch (e: Exception) {
                _error.value = "An unexpected error occurred: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUserProfile(updatedUser: User) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            userRepository.updateUserProfile(updatedUser)
                .onSuccess { _user.value = updatedUser }
                .onFailure { _error.value = it.message ?: "Failed to update user profile" }
            _isLoading.value = false
        }
    }

    fun toggleFollow(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = if (_isFollowing.value) {
                    userRepository.unfollowUser(userId)
                } else {
                    userRepository.followUser(userId)
                }
                result.onSuccess {
                    _isFollowing.value = !_isFollowing.value
                }.onFailure {
                    _error.value = it.message ?: "Failed to toggle follow"
                }
            } catch (e: Exception) {
                _error.value = "An unexpected error occurred: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun checkIfFollowing(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = userRepository.isFollowing(userId)
                result.onSuccess {
                    _isFollowing.value = it
                }.onFailure {
                    _error.value = it.message ?: "Failed to check follow status"
                }
            } catch (e: Exception) {
                _error.value = "An unexpected error occurred: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}