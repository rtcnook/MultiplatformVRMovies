package org.example.project.repository

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.example.project.domain.FilmItemModel

class FirebaseMainRepository : MainRepository {
    private val database = Firebase.database

    override fun getUpcomingFilms(): Flow<List<FilmItemModel>> =
        database.reference("Upcomming").valueEvents
            .map { snapshot ->
                snapshot.children.mapNotNull { child ->
                    runCatching { child.value<FilmItemModel>() }.getOrNull()
                }
            }
            .catch { emit(emptyList()) }

    override fun getItems(): Flow<List<FilmItemModel>> =
        database.reference("Items").valueEvents
            .map { snapshot ->
                snapshot.children.mapNotNull { child ->
                    runCatching { child.value<FilmItemModel>() }.getOrNull()
                }
            }
            .catch { emit(emptyList()) }
}

actual fun createMainRepository(): MainRepository = FirebaseMainRepository()
