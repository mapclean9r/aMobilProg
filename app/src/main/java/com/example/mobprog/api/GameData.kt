package com.example.mobprog.api

data class GameData(
    val id: Int,
    val title: String,
    val thumbnail: String,
    val status: String,
    val shortDescription: String,
    val description: String,
    val gameUrl: String,
    val genre: String,
    val platform: String,
    val publisher: String,
    val developer: String,
    val releaseDate: String,
    val freeToGameProfileUrl: String,
    val minimumSystemRequirements: MinimumSystemRequirements,
    val screenshots: List<Screenshot>
)

data class MinimumSystemRequirements(
    val os: String,
    val processor: String,
    val memory: String,
    val graphics: String,
    val storage: String
)

data class Screenshot(
    val id: Int,
    val image: String
)

