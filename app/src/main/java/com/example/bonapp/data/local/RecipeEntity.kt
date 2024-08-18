package com.example.bonapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val author: String,
    val categories: String,
    val dietType: String,
    val createdAt: Long,
    val updatedAt: Long,
    val prepTime: Int,
    val totalTime: Int,
    val yields: String,
    val ingredients: String,
    val instructions: String,
    val isPublic: Boolean,
    val localStatus: LocalStatus,
    val neededTools: String
)

enum class LocalStatus {
    UPLOADED,
    SAVED,
    PLANNED
}