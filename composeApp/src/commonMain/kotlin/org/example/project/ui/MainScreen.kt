package org.example.project.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.project.domain.FilmItemModel
import org.example.project.viewmodel.MainViewModel
import org.jetbrains.compose.resources.painterResource
import myvrmoviesmultiplatform.composeapp.generated.resources.*
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch

@Composable
fun MainScreen(onItemClick: (FilmItemModel) -> Unit = {}) {
    Scaffold(
        bottomBar = { BottomNavigationBar() },
        containerColor = BlackBackgroundColor,
        floatingActionButton = {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(PinkColor, GreenColor)
                        ),
                        shape = CircleShape
                    )
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                FloatingActionButton(
                    onClick = { },
                    containerColor = Black3Color,
                    contentColor = Color.White,
                    modifier = Modifier.size(58.dp),
                    shape = CircleShape
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.float_icon),
                        contentDescription = null,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(Res.drawable.bg1),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )
            MainContent(onItemClick)
        }
    }
}

@Composable
fun MainContent(onItemClick: (FilmItemModel) -> Unit) {
    val viewModel: MainViewModel = viewModel { MainViewModel() }
    val upcoming by viewModel.upcomingFilms.collectAsState(initial = emptyList<FilmItemModel>())
    val newMovies by viewModel.items.collectAsState(initial = emptyList<FilmItemModel>())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 48.dp, bottom = 100.dp)
    ) {
        Text(
            text = "What would you like to watch?",
            style = TextStyle(color = Color.White, fontSize = 25.sp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxWidth(),
            fontWeight = FontWeight.Bold
        )

        SearchBar(hint = "Search Movies...")
        
        SectionTitle("New Movies")
        if (newMovies.isEmpty()) {
            LoadingIndicator()
        } else {
            ScrollableLazyRow(items = newMovies, onItemClick = onItemClick)
        }

        SectionTitle("Upcoming Movies")
        if (upcoming.isEmpty()) {
            LoadingIndicator()
        } else {
            ScrollableLazyRow(items = upcoming, onItemClick = onItemClick)
        }
    }
}

/**
 * 支持鼠标滚轮横向滚动的 LazyRow。
 * 在 Desktop 端，鼠标滚轮默认只触发垂直滚动，
 * 这里通过 pointerInput 捕获滚轮事件并转换为水平滚动。
 */
@Composable
fun ScrollableLazyRow(
    items: List<FilmItemModel>,
    onItemClick: (FilmItemModel) -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        if (event.type == PointerEventType.Scroll) {
                            val scrollDelta = event.changes.firstOrNull()?.scrollDelta
                            if (scrollDelta != null) {
                                // 将垂直滚轮事件转为水平滚动（乘以系数提高灵敏度）
                                val deltaY = scrollDelta.y
                                if (deltaY != 0f) {
                                    coroutineScope.launch {
                                        listState.scrollBy(deltaY * 80f)
                                    }
                                    // 消费事件，防止冒泡到外层 verticalScroll
                                    event.changes.forEach { it.consume() }
                                }
                            }
                        }
                    }
                }
            }
    ) {
        items(items = items) { item: FilmItemModel ->
            FilmItem(item, onItemClick)
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = PinkColor)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = TextStyle(
            color = Color(0xFFFFC107),
            fontSize = 18.sp
        ),
        modifier = Modifier.padding(start = 16.dp, top = 32.dp, bottom = 8.dp),
        fontWeight = FontWeight.Bold
    )
}
