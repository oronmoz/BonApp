package com.example.bonapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bonapp.domain.model.User
import com.example.bonapp.ui.components.buttons.ProfileImage
import com.example.bonapp.ui.components.forms.RecipeCard
import com.example.bonapp.ui.viewmodel.ProfileViewModel
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String,
    onNavigateToRecipe: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val userRecipes by viewModel.userRecipes.collectAsState()
    val isCurrentUser by viewModel.isCurrentUser.collectAsState()
    val isFollowing by viewModel.isFollowing.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            viewModel.loadUserProfile(userId)
            viewModel.loadUserRecipes(userId)
            viewModel.checkIfCurrentUser(userId)
            viewModel.checkIfFollowing(userId)
        }
    }

    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(error) {
        error?.let {
            showErrorDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(userProfile?.name ?: "Profile") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    UserProfileHeader(
                        userProfile = userProfile,
                        isCurrentUser = isCurrentUser,
                        isFollowing = isFollowing,
                        onFollowClick = {
                            viewModel.toggleFollow(userId)
                        }
                    )
                }

                item {
                    Text(
                        "Recipes",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                items(userRecipes) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onClick = { onNavigateToRecipe(recipe.id) },
                        onFavoriteClick = { /* Handle favorite click */ },
                        onPlannedClick = { /* Handle planned click */ },
                        onEditClick = null,
                        currentUserId = viewModel.getCurrentUserId()
                    )
                }
            }

            if (userProfile == null && userRecipes.isEmpty() && error == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
                viewModel.clearError()
            },
            title = { Text("Error") },
            text = { Text(error ?: "An unknown error occurred") },
            confirmButton = {
                TextButton(onClick = {
                    showErrorDialog = false
                    viewModel.clearError()
                }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun UserProfileHeader(
    userProfile: User?,
    isCurrentUser: Boolean,
    isFollowing: Boolean,
    onFollowClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileImage(
            imageUrl = userProfile?.profileImageUrl,
            onImageClick = { /* Handle image click if needed */ },
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = userProfile?.name ?: "Loading...",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = userProfile?.email ?: "",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = userProfile?.about ?: "",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Display follower and following counts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Followers: ${userProfile?.followers?.size ?: 0}")
            Text("Following: ${userProfile?.following?.size ?: 0}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Always show the follow button for non-current users
        if (!isCurrentUser && userProfile != null) {
            Button(onClick = onFollowClick) {
                Text(if (isFollowing) "Unfollow" else "Follow")
            }
        }
    }
}