package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.example.project.domain.FilmItemModel
import org.example.project.ui.DetailScreen
import org.example.project.ui.IntroScreen
import org.example.project.ui.LoginScreen
import org.example.project.ui.MainScreen

@Serializable
private sealed interface AppRoute : NavKey

@Serializable
private data object SplashRoute : AppRoute

@Serializable
private data object LoginRoute : AppRoute

@Serializable
private data object MainRoute : AppRoute

@Serializable
private data class DetailRoute(val film: FilmItemModel) : AppRoute

private val navSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = SerializersModule {
        polymorphic(NavKey::class) {
            subclass(SplashRoute::class, SplashRoute.serializer())
            subclass(LoginRoute::class, LoginRoute.serializer())
            subclass(MainRoute::class, MainRoute.serializer())
            subclass(DetailRoute::class, DetailRoute.serializer())
        }
    }
}

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        getAsyncImageLoader(context)
    }

    MaterialTheme {
        val backStack = rememberNavBackStack(navSavedStateConfiguration, SplashRoute)

        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = { route ->
                when (route) {
                    SplashRoute -> NavEntry(route) {
                        IntroScreen(
                            onGetInClick = { backStack.add(LoginRoute) }
                        )
                    }

                    LoginRoute -> NavEntry(route) {
                        LoginScreen(
                            onLoginClick = { backStack.add(MainRoute) }
                        )
                    }

                    MainRoute -> NavEntry(route) {
                        MainScreen(
                            onItemClick = { film -> backStack.add(DetailRoute(film)) }
                        )
                    }

                    is DetailRoute -> NavEntry(route) {
                        DetailScreen(
                            film = route.film,
                            onBackClick = { backStack.removeLastOrNull() }
                        )
                    }

                    else -> error("Unknown route: $route")
                }
            }
        )
    }
}
