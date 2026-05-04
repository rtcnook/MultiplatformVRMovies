package org.example.project

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize

fun main() {
    // Desktop 端现在使用本地 JSON 数据源，不再需要手动初始化 Firebase
    
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "MyVRMoviesMultiplatform",
        ) {
            App()
        }
    }
}