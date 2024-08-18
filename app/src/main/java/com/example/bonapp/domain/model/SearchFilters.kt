package com.example.bonapp.domain.model

data class SearchFilters(
    val name: String = "",
    val categories: List<String> = emptyList(),
    val dietTypes: List<String> = emptyList(),
    val minPrepTime: Int? = null,
    val maxPrepTime: Int? = null,
    val difficulties: List<String> = emptyList()
)