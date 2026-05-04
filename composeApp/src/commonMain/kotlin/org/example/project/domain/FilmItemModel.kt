package org.example.project.domain

import kotlinx.serialization.Serializable

@Serializable
data class FilmItemModel(
    val Title: String = "",
    val Description: String = "",
    val Poster: String = "",
    val Time: String = "",
    val Trailer: String = "",
    val Imdb: Double = 0.0,       // Firebase 中可能是数字类型
    val Year: Int = 0,
    val price: Double = 0.0,
    val Genre: List<String> = emptyList(),
    val Casts: List<CastModel> = emptyList()
)
