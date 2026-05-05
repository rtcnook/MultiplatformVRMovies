package org.example.project.repository

actual fun defaultServerBaseUrl(): String = "http://localhost:8080"

actual fun createMainRepository(): MainRepository = ServerMainRepository()
