package org.example.project.repository

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.example.project.platformLog
import org.example.project.domain.CastModel
import org.example.project.domain.FilmItemModel

@Serializable
private data class RemoteDataWrapper(
    val Items: List<FilmItemModel> = emptyList(),
    val Upcomming: List<FilmItemModel> = emptyList()
)

class ServerMainRepository(
    private val baseUrl: String = defaultServerBaseUrl()
) : MainRepository {
    private val client = HttpClient()
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private suspend fun loadData(): RemoteDataWrapper {
        return try {
            val response = client.get("$baseUrl/api/movies").bodyAsText()
            json.decodeFromString<RemoteDataWrapper>(response.trimStart('\uFEFF'))
                .withResolvedMediaUrls()
                .also { data ->
                    val firstPoster = data.Items.firstOrNull()?.Poster ?: data.Upcomming.firstOrNull()?.Poster
                    platformLog("MyVRMovies", "Loaded movies from $baseUrl, first poster: $firstPoster")
                }
        } catch (e: Exception) {
            platformLog("MyVRMovies", "Error loading movies from server: ${e.message}")
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

expect fun defaultServerBaseUrl(): String

private fun RemoteDataWrapper.withResolvedMediaUrls(): RemoteDataWrapper =
    copy(
        Items = Items.map { it.withResolvedMediaUrls() },
        Upcomming = Upcomming.map { it.withResolvedMediaUrls() }
    )

private fun FilmItemModel.withResolvedMediaUrls(): FilmItemModel =
    copy(
        Poster = resolveServerMediaUrl(Poster),
        Casts = Casts.map { it.withResolvedMediaUrls() }
    )

private fun CastModel.withResolvedMediaUrls(): CastModel =
    copy(PicUrl = resolveServerMediaUrl(PicUrl))

private fun resolveServerMediaUrl(url: String): String =
    when {
        url.startsWith("http://") || url.startsWith("https://") -> url
        url.startsWith("/") -> defaultServerBaseUrl().trimEnd('/') + url
        else -> url
    }
