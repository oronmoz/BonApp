package com.example.bonapp.domain.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val diet: String = "",
    val about: String = "",
    val profileImageUrl: String? = null,
    val socialMediaLinks: Map<String, String> = emptyMap(),
    val favoriteTagsAndCategories: List<String> = emptyList(),
    val favorites: List<String> = emptyList(),
    val plannedList: List<String> = emptyList(),
    val reviews: List<String> = emptyList(),
    val followers: List<String> = emptyList(),
    val following: List<String> = emptyList()
)

