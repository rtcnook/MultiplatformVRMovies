package org.example.project.repository

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.example.project.domain.FilmItemModel

private const val SERVER_BASE_URL = "http://192.168.2.12:8080"

@Serializable
private data class RemoteDataWrapper(
    val Items: List<FilmItemModel> = emptyList(),
    val Upcomming: List<FilmItemModel> = emptyList()
)

class ServerMainRepository(
    private val baseUrl: String = SERVER_BASE_URL
) : MainRepository {
    private val client = HttpClient(CIO)
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private suspend fun loadData(): RemoteDataWrapper {
        return try {
            val response = client.get("$baseUrl/api/movies").bodyAsText()
            json.decodeFromString<RemoteDataWrapper>(response)
        } catch (e: Exception) {
            println("Error loading movies from server: ${e.message}")
            RemoteDataWrapper()
        }
    }

    override fun getUpcomingFilms(): Flow<List<FilmItemModel>> = flow {
        emit(loadData().Upcomming)
    }

    override fun getItems(): Flow<List<FilmItemModel>> = flow {
        emit(loadData().Items)
    }
}

actual fun createMainRepository(): MainRepository = ServerMainRepository()
