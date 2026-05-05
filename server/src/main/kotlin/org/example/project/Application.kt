package org.example.project

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.ContentType
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }

        get("/api/movies") {
            call.respondText(loadMoviesJson(), ContentType.Application.Json)
        }

        staticFiles("/files", findMoviesDataDirectory().toFile())
    }
}

private fun loadMoviesJson(): String {
    val dataFile = findMoviesDataDirectory().resolve("database.json")
    return Files.readString(dataFile)
}

private fun findMoviesDataDirectory(): Path {
    val candidates = listOf(
        Paths.get("composeApp", "src", "commonMain", "composeResources", "files"),
        Paths.get("..", "composeApp", "src", "commonMain", "composeResources", "files")
    )

    return candidates.firstOrNull { Files.isDirectory(it) }
        ?: error("Cannot find composeResources/files")
}
