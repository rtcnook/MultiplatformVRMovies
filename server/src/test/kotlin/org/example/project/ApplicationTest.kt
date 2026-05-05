package org.example.project

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Ktor: ${Greeting().greet()}", response.bodyAsText())
    }

    @Test
    fun testMoviesApi() = testApplication {
        application {
            module()
        }
        val response = client.get("/api/movies")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ContentType.Application.Json, response.contentType()?.withoutParameters())
        val body = response.bodyAsText()
        assertTrue(body.contains("\"Items\""))
        assertTrue(body.contains("\"Upcomming\""))
        assertTrue(body.contains("/media/posters/bad-boys.jpg"))
        assertTrue(body.contains("/media/actors/will-smith.jpg"))
    }

    @Test
    fun testMediaAliasRoute() = testApplication {
        application {
            module()
        }
        val response = client.get("/media/posters/bad-boys.jpg")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(ContentType.Image.JPEG, response.contentType()?.withoutParameters())
    }
}
