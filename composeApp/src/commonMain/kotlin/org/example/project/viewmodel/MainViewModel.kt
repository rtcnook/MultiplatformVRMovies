package org.example.project.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.project.domain.FilmItemModel
import org.example.project.repository.MainRepository
import org.example.project.repository.createMainRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: MainRepository = createMainRepository()
) : ViewModel() {

    private val _upcomingFilms = MutableStateFlow<List<FilmItemModel>>(emptyList())
    val upcomingFilms: StateFlow<List<FilmItemModel>> = _upcomingFilms.asStateFlow()

    private val _items = MutableStateFlow<List<FilmItemModel>>(emptyList())
    val items: StateFlow<List<FilmItemModel>> = _items.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getUpcomingFilms()
                .catch { e ->
                    println("加载 Upcoming 失败: ${e.message}")
                }
                .collect { films ->
                    _upcomingFilms.value = films
                }
        }

        viewModelScope.launch {
            repository.getItems()
                .catch { e -> println("加载 Items 失败: ${e.message}") }
                .collect { films ->
                    _items.value = films
                }
        }
    }
}
