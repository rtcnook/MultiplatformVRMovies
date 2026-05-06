package org.example.project

actual fun platformLog(tag: String, message: String) {
    println("$tag: $message")
}
