package com.example.bonapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bonapp.ui.components.buttons.ProfileImage
import com.example.bonapp.ui.viewmodel.UserViewModel

@Composable
fun EditProfileScreen(
    viewModel: UserViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var name by remember { mutableStateOf("") }
    var diet by remember { mutableStateOf("") }
    var about by remember { mutableStateOf("") }

    LaunchedEffect(user) {
        user?.let {
            name = it.name
            diet = it.diet
            about = it.about
        }
    }

    LaunchedEffect(error) {
        error?.let {
            // Show error message (you can use a Snackbar or Dialog here)
            viewModel.clearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        user?.let { currentUser ->
            ProfileImage(
                imageUrl = currentUser.profileImageUrl,
                onImageClick = { /* Implement image picker here */ },
                modifier = Modifier.size(120.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = diet,
            onValueChange = { diet = it },
            label = { Text("Diet") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = about,
            onValueChange = { about = it },
            label = { Text("About") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                user?.let {
                    viewModel.updateUserProfile(it.copy(name = name, diet = diet, about = about))
                    onNavigateBack()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            Text("Save Changes")
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}