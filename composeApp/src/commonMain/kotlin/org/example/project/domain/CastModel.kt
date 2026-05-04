package org.example.project.domain

import kotlinx.serialization.Serializable

@Serializable
data class CastModel(
    val PicUrl: String = "",
    val Actor: String = ""
)
