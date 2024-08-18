package com.example.bonapp.ui.auth

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bonapp.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()

    AuthScreen(
        isLogin = true,
        onAuthAction = { userData ->
            viewModel.login(
                email = userData["email"] ?: "",
                password = userData["password"] ?: ""
            )
        },
        onNavigateToOtherAuth = onNavigateToRegister,
        authState = authState,
        onAuthSuccess = onLoginSuccess
    )
}