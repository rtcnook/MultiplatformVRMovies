package org.example.project.repository

actual fun createMainRepository(): MainRepository = LocalJsonMainRepository()
