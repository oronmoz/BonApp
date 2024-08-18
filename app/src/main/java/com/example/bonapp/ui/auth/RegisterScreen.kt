package com.example.bonapp.ui.auth
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bonapp.ui.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()

    AuthScreen(
        isLogin = false,
        onAuthAction = { userData ->
            if (userData["password"] == userData["confirmPassword"]) {
                viewModel.register(
                    name = userData["name"] ?: "",
                    email = userData["email"] ?: "",
                    password = userData["password"] ?: "",
                    diet = userData["diet"] ?: "",
                    about = userData["about"] ?: ""
                )
            } else {
                // Handle password mismatch error
                viewModel.setError("Passwords do not match")
            }
        },
        onNavigateToOtherAuth = onNavigateToLogin,
        authState = authState,
        onAuthSuccess = onRegisterSuccess
    )
}