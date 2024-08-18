package com.example.bonapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bonapp.ui.main.RecipesScreen
import com.example.bonapp.ui.auth.LoginScreen
import com.example.bonapp.ui.auth.RegisterScreen
import com.example.bonapp.ui.main.*
import com.example.bonapp.ui.profile.EditProfileScreen
import com.example.bonapp.ui.main.MyAccountScreen
import com.example.bonapp.ui.profile.ProfileScreen
import com.example.bonapp.ui.recipes.AddRecipeScreen
import com.example.bonapp.ui.recipes.EditRecipeScreen
import com.example.bonapp.ui.recipes.RecipeDetailScreen
import com.example.bonapp.ui.main.HomeScreen


sealed class Screen(val route: String, val icon: ImageVector? = null, val label: String? = null) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home", Icons.Default.Home, "Home")
    object Recipes : Screen("recipes", Icons.AutoMirrored.Filled.List, "Recipes")
    object MyAccount : Screen("my_account", Icons.Default.Person, "My Account")
    object RecipeDetail : Screen("recipe/{recipeId}") {
        fun createRoute(recipeId: String) = "recipe/$recipeId"
    }

    object EditRecipe : Screen("edit_recipe/{recipeId}") {
        fun createRoute(recipeId: String) = "edit_recipe/$recipeId"
    }

    object AddRecipe : Screen("add_recipe")
    object EditProfile : Screen("edit_profile")
    object Profile : Screen("profile/{userId}") {
        fun createRoute(userId: String) = "profile/$userId"
    }

    object Search : Screen("search?query={query}") {
        fun createRoute(query: String) = "search?query=$query"
    }

}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar(navController)) {
                NavigationBar {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    listOf(Screen.Home, Screen.Recipes, Screen.MyAccount).forEach { screen ->
                        NavigationBarItem(
                            icon = { screen.icon?.let { Icon(it, contentDescription = null) } },
                            label = { Text(screen.label ?: "") },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { navController.navigate(Screen.Home.route) },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = { navController.navigate(Screen.Home.route) },
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToAddRecipe = { navController.navigate(Screen.AddRecipe.route) },
                    onNavigateToRecipeDetail = { recipeId ->
                        navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                    }
                )
            }
            composable(Screen.Recipes.route) {
                RecipesScreen(
                    onNavigateToRecipeDetail = { recipeId ->
                        navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                    },
                    onNavigateToEditRecipe = { recipeId ->
                        navController.navigate(Screen.EditRecipe.createRoute(recipeId))
                    }
                )
            }
            composable(Screen.MyAccount.route) {
                MyAccountScreen(
                    onNavigateToEditProfile = { navController.navigate(Screen.EditProfile.route) }
                )
            }
            composable(Screen.EditProfile.route) {
                EditProfileScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.RecipeDetail.route) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
                RecipeDetailScreen(
                    recipeId = recipeId,
                    onNavigateToEdit = { id ->
                        navController.navigate(Screen.EditRecipe.createRoute(id))
                    },
                    onNavigateToAuthor = { authorId ->
                        navController.navigate(Screen.Profile.createRoute(authorId))
                    },
                    onNavigateToSearch = { query ->
                        navController.navigate(Screen.Search.createRoute(query))
                    }
                )
            }
            composable(Screen.EditRecipe.route) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
                EditRecipeScreen(
                    recipeId = recipeId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.AddRecipe.route) {
                AddRecipeScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            composable(Screen.Profile.route) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                ProfileScreen(
                    userId = userId,
                    onNavigateToRecipe = { recipeId ->
                        navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                    }
                )
            }
            composable(Screen.Search.route) { backStackEntry ->
                val initialQuery = backStackEntry.arguments?.getString("query") ?: ""
                SearchScreen(
                    initialQuery = initialQuery,
                    onNavigateToRecipe = { recipeId ->
                        navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                    }
                )
            }
        }
    }
}

@Composable
fun shouldShowBottomBar(navController: NavHostController): Boolean {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    return currentRoute in listOf(Screen.Home.route, Screen.Recipes.route, Screen.MyAccount.route)
}