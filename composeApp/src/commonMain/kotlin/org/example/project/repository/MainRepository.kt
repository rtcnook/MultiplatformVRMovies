package org.example.project.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.FilmItemModel

interface MainRepository {
    fun getUpcomingFilms(): Flow<List<FilmItemModel>>
    fun getItems(): Flow<List<FilmItemModel>>
}

expect fun createMainRepository(): MainRepository
