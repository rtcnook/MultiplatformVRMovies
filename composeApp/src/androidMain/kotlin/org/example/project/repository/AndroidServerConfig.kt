package org.example.project.repository

actual fun defaultServerBaseUrl(): String = "http://192.168.2.12:8080"

actual fun createMainRepository(): MainRepository = ServerMainRepository()
