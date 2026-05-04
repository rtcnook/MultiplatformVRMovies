package org.example.project.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import myvrmoviesmultiplatform.composeapp.generated.resources.Res
import org.example.project.domain.FilmItemModel
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Serializable
private data class LocalDataWrapper(
    val Items: List<FilmItemModel> = emptyList(),
    val Upcomming: List<FilmItemModel> = emptyList()
)

class LocalJsonMainRepository : MainRepository {
    private val json = Json { 
        ignoreUnknownKeys = true 
        coerceInputValues = true
    }

    private suspend fun loadData(): LocalDataWrapper {
        return try {
            val bytes = Res.readBytes("files/database.json")
            val content = bytes.decodeToString()
            json.decodeFromString<LocalDataWrapper>(content)
        } catch (e: Exception) {
            println("Error loading local JSON: ${e.message}")
            LocalDataWrapper()
        }
    }

    override fun getUpcomingFilms(): Flow<List<FilmItemModel>> = flow {
        emit(loadData().Upcomming)
    }

    override fun getItems(): Flow<List<FilmItemModel>> = flow {
        emit(loadData().Items)
    }
}
