package com.example.bonapp.ui.recipes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.bonapp.domain.model.Recipe
import com.example.bonapp.ui.components.buttons.*
import com.example.bonapp.ui.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: String,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToAuthor: (String) -> Unit,
    onNavigateToSearch: (String) -> Unit,
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val recipe by viewModel.currentRecipe.collectAsState()
    val recipeState by viewModel.recipeState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    val isPlanned by viewModel.isPlanned.collectAsState()
    val author by viewModel.author.collectAsState()
    val currentUserId = viewModel.getCurrentUserId()

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipe Details") },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite"
                        )
                    }
                    IconButton(onClick = { viewModel.togglePlanned() }) {
                        Icon(
                            imageVector = if (isPlanned) Icons.Filled.DateRange else Icons.Outlined.DateRange,
                            contentDescription = "Planned"
                        )
                    }
                    if (recipe?.author == currentUserId) {
                        IconButton(onClick = { recipe?.id?.let { onNavigateToEdit(it) } }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Recipe")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when (recipeState) {
                is RecipeViewModel.RecipeState.Loading -> CircularProgressIndicator()
                is RecipeViewModel.RecipeState.Error -> Text(
                    text = (recipeState as RecipeViewModel.RecipeState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
                is RecipeViewModel.RecipeState.Success -> {
                    recipe?.let { currentRecipe ->
                        RecipeDetails(
                            recipe = currentRecipe,
                            onAuthorClick = onNavigateToAuthor,
                            onTagClick = onNavigateToSearch,
                            viewModel = viewModel
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeDetails(
    recipe: Recipe,
    onAuthorClick: (String) -> Unit,
    onTagClick: (String) -> Unit,
    viewModel: RecipeViewModel
) {
    val author by viewModel.author.collectAsState()

    Column {
        Text(recipe.name, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        // Image Carousel
        val mediaItems = listOfNotNull(recipe.videoLink) + recipe.pictureUrls
        if (mediaItems.isNotEmpty()) {
            ImageCarousel(
                images = mediaItems,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Author Card
        author?.let { authorData ->
            AuthorCard(
                author = authorData,
                onAuthorClick = { onAuthorClick(authorData.id) }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Rating
        // Rating
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Rating: ${String.format("%.1f", recipe.rating)}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.width(8.dp))
            RatingBar(rating = recipe.rating)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "(${recipe.reviews.size} reviews)",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Categories and Diet Type
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            recipe.categories.forEach { category ->
                SelectableTag(name = category, onTagClick = { onTagClick(category) })
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        recipe.dietTypes.forEach { dietType ->
            SelectableTag(name = dietType, onTagClick = { onTagClick(dietType) })
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Sponsorship
        recipe.sponsorship?.let {
            Text("Sponsored by: $it", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Video
        recipe.videoLink?.let {
            VideoPlayer(videoUrl = it)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // About
        recipe.about?.let {
            Text("About:", style = MaterialTheme.typography.titleMedium)
            Text(it)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Preparation Time
        Text("Preparation Time: ${recipe.prepTime ?: "N/A"} minutes")
        Text("Total Time: ${recipe.totalTime} minutes")
        Text("Yields: ${recipe.yields}")
        recipe.caloriesPerServing?.let { Text("Calories per Serving: $it") }
        Spacer(modifier = Modifier.height(16.dp))

        // Ingredients
        Text("Ingredients:", style = MaterialTheme.typography.titleMedium)
        recipe.components.forEach { component ->
            Text(component.title ?: "Ingredients", style = MaterialTheme.typography.titleSmall)
            component.ingredients.forEach { ingredient ->
                Text("• ${ingredient.amount} ${ingredient.unit} ${ingredient.name}")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Instructions
        Text("Instructions:", style = MaterialTheme.typography.titleMedium)
        recipe.instructions.forEachIndexed { index, instruction ->
            Text("${index + 1}. $instruction")
        }

        // Notes
        recipe.notes?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Notes:", style = MaterialTheme.typography.titleMedium)
            Text(it)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Comments and Reviews
        var selectedTab by remember { mutableStateOf(0) }
        val tabs = listOf("Comments", "Reviews")

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        when (selectedTab) {
            0 -> CommentSection(
                comments = recipe.comments,
                onCommentPosted = { comment -> viewModel.addComment(comment) }
            )
            1 -> ReviewSection(
                reviews = recipe.reviews,
                onReviewPosted = { rating, review -> viewModel.addReview(rating, review) }
            )
        }
    }
}

@Composable
fun CommentSection(comments: List<Recipe.Comment>, onCommentPosted: (String) -> Unit) {
    var newComment by remember { mutableStateOf("") }

    Column {
        comments.forEach { comment ->
            CommentCard(comment = Comment(comment.userId, comment.comment, comment.timestamp))
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = newComment,
            onValueChange = { newComment = it },
            label = { Text("Add a comment") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )
        Button(
            onClick = {
                onCommentPosted(newComment)
                newComment = ""
            },
            enabled = newComment.isNotBlank(),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Post")
        }
    }
}

@Composable
fun ReviewSection(reviews: List<Recipe.Review>, onReviewPosted: (Int, String) -> Unit) {
    var newReviewContent by remember { mutableStateOf("") }
    var newRating by remember { mutableStateOf(0) }

    Column {
        reviews.forEach { review ->
            ReviewCard(review = Review(review.userId, review.rating.toFloat(), review.comment, 0L))
            Spacer(modifier = Modifier.height(8.dp))
        }

        Text("Your Rating:")
        Slider(
            value = newRating.toFloat(),
            onValueChange = { newRating = it.toInt() },
            valueRange = 0f..5f,
            steps = 4
        )

        OutlinedTextField(
            value = newReviewContent,
            onValueChange = { newReviewContent = it },
            label = { Text("Write your review") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3
        )
        Button(
            onClick = {
                onReviewPosted(newRating, newReviewContent)
                newReviewContent = ""
                newRating = 0
            },
            enabled = newReviewContent.isNotBlank() && newRating > 0,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Post Review")
        }
    }
}