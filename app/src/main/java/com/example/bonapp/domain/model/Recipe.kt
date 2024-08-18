package com.example.bonapp.domain.model

import com.google.firebase.firestore.PropertyName

data class Recipe(
    @JvmField @PropertyName("id") val id: String = "",
    @JvmField @PropertyName("name") val name: String = "",
    @JvmField @PropertyName("author") val author: String = "",
    @JvmField @PropertyName("categories") val categories: List<String> = emptyList(),
    @JvmField @PropertyName("dietType") val dietTypes: List<String> = emptyList(),
    @JvmField @PropertyName("createdAt") val createdAt: Long = 0,
    @JvmField @PropertyName("updatedAt") val updatedAt: Long = 0,
    @JvmField @PropertyName("sponsorship") val sponsorship: String? = null,
    @JvmField @PropertyName("pictureUrls") val pictureUrls: List<String> = emptyList(),
    @JvmField @PropertyName("videoLink") val videoLink: String? = null,
    @JvmField @PropertyName("about") val about: String? = null,
    @JvmField @PropertyName("inspiredByLinks") val inspiredByLinks: List<String>? = null,
    @JvmField @PropertyName("rating") val rating: Double = 0.0,
    @JvmField @PropertyName("prepTime") val prepTime: Int? = null,
    @JvmField @PropertyName("totalTime") val totalTime: Int = 0,
    @JvmField @PropertyName("yields") val yields: String = "",
    @JvmField @PropertyName("caloriesPerServing") val caloriesPerServing: Int? = null,
    @JvmField @PropertyName("neededTools") val neededTools: List<String>? = null,
    @JvmField @PropertyName("components") val components: List<Component> = emptyList(),
    @JvmField @PropertyName("instructions") val instructions: List<String> = emptyList(),
    @JvmField @PropertyName("notes") val notes: String? = null,
    @JvmField @PropertyName("comments") val comments: List<Comment> = emptyList(),
    @JvmField @PropertyName("reviews") val reviews: List<Review> = emptyList(),
    @JvmField @PropertyName("isPublic") val isPublic: Boolean = false,
    @JvmField @PropertyName("isFavorite") val isFavorite: Boolean = false,
    @JvmField @PropertyName("isPlanned") val isPlanned: Boolean = false,
    @JvmField @PropertyName("numberOfReviews") val numberOfReviews: Int = 0,
    @JvmField @PropertyName("numberOfComments") val numberOfComments: Int = 0
) {
    // No-arg constructor for Firestore
    constructor() : this(id = "")

    data class Component(
        @JvmField @PropertyName("title") val title: String? = null,
        @JvmField @PropertyName("ingredients") val ingredients: List<Ingredient> = emptyList()
    )

    data class Ingredient(
        @JvmField @PropertyName("name") val name: String = "",
        @JvmField @PropertyName("amount") val amount: String = "",
        @JvmField @PropertyName("unit") val unit: String = ""
    )

    data class Review(
        @JvmField @PropertyName("userId") val userId: String = "",
        @JvmField @PropertyName("rating") val rating: Int = 0,
        @JvmField @PropertyName("comment") val comment: String = ""
    )

    data class Comment(
        @JvmField @PropertyName("userId") val userId: String = "",
        @JvmField @PropertyName("comment") val comment: String = "",
        @JvmField @PropertyName("timestamp") val timestamp: Long = System.currentTimeMillis()
    )

}