package com.example.bonapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bonapp.ui.main.RecipesScreen
import com.example.bonapp.ui.recipes.AddRecipeScreen
import com.example.bonapp.ui.main.HomeScreen


@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                listOf(
                    Screen.Home,
                    Screen.Recipes,
                    Screen.MyAccount
                ).forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon!!, contentDescription = null) },
                        label = { Text(screen.label!!) },
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToAddRecipe = {
                        navController.navigate(Screen.AddRecipe.route)
                    },
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
            //composable(Screen.MyAccount.route) { MyAccountScreen() }
            composable(Screen.AddRecipe.route) {
                AddRecipeScreen(
                    onNavigateBack = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}

//sealed class Screen(val route: String, val icon: ImageVector? = null, val label: String? = null) {
//    object Home : Screen("home", "Home", Icons.Default.Home)
//    object Recipes : Screen("recipes", "Recipes", Icons.AutoMirrored.Filled.List)
//    object MyAccount : Screen("my_account", "My Account", Icons.Default.Person)
//    object AddRecipe : Screen("add_recipe", "Add Recipe", Icons.Default.Add)
//    object EditProfile : Screen("edit_profile")
//}