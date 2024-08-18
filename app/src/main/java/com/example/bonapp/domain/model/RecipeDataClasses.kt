package com.example.bonapp.domain.model

data class Component(
    val name: String,
    val ingredients: List<Ingredient>
)

data class Ingredient(
    val amount: String,
    val unit: String,
    val name: String
)

data class Instruction(
    val name: String,
    val body: String
)