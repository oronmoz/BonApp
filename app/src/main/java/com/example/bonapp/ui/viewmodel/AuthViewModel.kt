package com.example.bonapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bonapp.domain.model.User
import com.example.bonapp.domain.repository.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    init {
        tryAutoLogin()
    }

    private fun tryAutoLogin() {
        viewModelScope.launch {
            val result = repository.autoLogin()
            if (result != null) {
                _authState.value = AuthState.Success(result.getOrNull()!!)
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.login(email, password)
            _authState.value = when {
                result.isSuccess -> AuthState.Success(result.getOrNull()!!)
                else -> AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun register(name: String, email: String, password: String, diet: String, about: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val user = User(
                name = name,
                email = email,
                diet = diet,
                about = about
                // Initialize other fields as needed
            )
            val result = repository.register(user, password)
            _authState.value = when {
                result.isSuccess -> AuthState.Success(result.getOrNull()!!)
                else -> AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _authState.value = AuthState.Initial
        }
    }

    fun getUserProfile() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.getUserProfile()
            _authState.value = when {
                result.isSuccess -> AuthState.Success(result.getOrNull()!!)
                else -> AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun updateUserProfile(user: User) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.updateUserProfile(user)
            _authState.value = when {
                result.isSuccess -> AuthState.Success(user)
                else -> AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    sealed class AuthState {
        object Initial : AuthState()
        object Loading : AuthState()
        data class Success(val user: User) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    fun setError(message: String) {
        _authState.value = AuthState.Error(message)
    }
}