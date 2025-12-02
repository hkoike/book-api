package com.example.hkoike.codingtest.bookapi.presentation.controller

import com.example.hkoike.codingtest.bookapi.application.service.AuthorService
import com.example.hkoike.codingtest.bookapi.domain.model.Author
import com.example.hkoike.codingtest.bookapi.presentation.dto.AuthorRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.time.LocalDate

@WebMvcTest(AuthorController::class)
class AuthorControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var authorService: AuthorService

    @Test
    fun `POST v1-authors 正常に著者が作成される`() {
        val requestJson = """
            {
                "name": "author1",
                "birthDate": "1990-01-01"
            }
        """.trimIndent()

        val saved =
            Author(
                id = 1L,
                name = "author1",
                birthDate = LocalDate.of(1990, 1, 1),
            )

        whenever(authorService.createAuthor(any())).thenReturn(saved)

        mockMvc.post("/v1/authors") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = requestJson
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(1) }
            jsonPath("$.name") { value("author1") }
            jsonPath("$.birthDate") { value("1990-01-01") }
        }
    }

    @Test
    fun `PUT v1-authors-id 正常に著者が更新される`() {
        val id = 5L

        val requestJson = """
            {
                "name": "updated author",
                "birthDate": "1985-05-05"
            }
        """.trimIndent()

        val updated =
            Author(
                id = id,
                name = "updated author",
                birthDate = LocalDate.of(1985, 5, 5),
            )

        whenever(authorService.updateAuthor(eq(id), any()))
            .thenReturn(updated)

        mockMvc.put("/v1/authors/{id}", id) {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = requestJson
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(id.toInt()) }
            jsonPath("$.name") { value("updated author") }
            jsonPath("$.birthDate") { value("1985-05-05") }
        }
    }
}
