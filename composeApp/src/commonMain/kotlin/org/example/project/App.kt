package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import org.example.project.domain.FilmItemModel
import org.example.project.ui.DetailScreen
import org.example.project.ui.IntroScreen
import org.example.project.ui.LoginScreen
import org.example.project.ui.MainScreen

// 导航状态
sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object Main : Screen()
    data class Detail(val film: FilmItemModel) : Screen()
}

// 暴露一个全局的"可供平台调用的返回函数"
object AppNavigator {
    var onBack: (() -> Boolean)? = null
}

@Composable
fun App() {
    // 配置 Coil 3 的网络引擎
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }
            .build()
    }

    MaterialTheme {
        // 用列表作为返回栈
        val backStack = remember { mutableStateListOf<Screen>(Screen.Splash) }
        val currentScreen = backStack.last()

        // 注册返回处理函数，供各平台（MainActivity 等）调用
        DisposableEffect(Unit) {
            AppNavigator.onBack = {
                if (backStack.size > 1) {
                    backStack.removeLastOrNull()
                    true // 已消耗，不退出 App
                } else {
                    false // 没有更多页面，让平台自行退出
                }
            }
            onDispose { AppNavigator.onBack = null }
        }

        fun navigate(screen: Screen) {
            backStack.add(screen)
        }

        when (val screen = currentScreen) {
            is Screen.Splash -> IntroScreen(
                onGetInClick = { navigate(Screen.Login) }
            )
            is Screen.Login -> LoginScreen(
                onLoginClick = { navigate(Screen.Main) }
            )
            is Screen.Main -> MainScreen(
                onItemClick = { film -> navigate(Screen.Detail(film)) }
            )
            is Screen.Detail -> DetailScreen(
                film = screen.film,
                onBackClick = { backStack.removeLastOrNull() }
            )
        }
    }
}