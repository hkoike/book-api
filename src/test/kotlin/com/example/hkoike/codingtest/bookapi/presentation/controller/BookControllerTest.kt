package com.example.hkoike.codingtest.bookapi.presentation.controller

import com.example.hkoike.codingtest.bookapi.application.service.BookService
import com.example.hkoike.codingtest.bookapi.domain.exception.BookNotFoundException
import com.example.hkoike.codingtest.bookapi.domain.exception.InvalidBookOperationException
import com.example.hkoike.codingtest.bookapi.domain.model.Book
import com.example.hkoike.codingtest.bookapi.domain.model.PublicationStatus
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.given
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.time.LocalDate

@WebMvcTest(BookController::class)
class BookControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var bookService: BookService

    @Test
    fun `authorIdsを指定して紐づく本を返す`() {
        // given
        val authorIds = listOf(1L, 2L)

        val books =
            listOf(
                Book(
                    id = 1L,
                    title = "book1",
                    price = 1000,
                    status = PublicationStatus.UNPUBLISHED,
                    publishedAt = LocalDate.of(2024, 1, 1),
                    authorIds = listOf(1L),
                ),
                Book(
                    id = 2L,
                    title = "book2",
                    price = 2000,
                    status = PublicationStatus.PUBLISHED,
                    publishedAt = LocalDate.of(2024, 2, 1),
                    authorIds = listOf(2L, 3L),
                ),
            )

        whenever(bookService.getBooksByAuthors(authorIds)).thenReturn(books)

        mockMvc
            .get("/v1/books") {
                param("authorIds", "1", "2")
                accept = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isOk() }

                // 配列の長さ = 2 件
                jsonPath("$.length()") { value(2) }

                // 1件目
                jsonPath("$[0].id") { value(1) }
                jsonPath("$[0].title") { value("book1") }
                jsonPath("$[0].price") { value(1000) }
                jsonPath("$[0].status") { value("UNPUBLISHED") }
                jsonPath("$[0].authorIds[0]") { value(1) }

                // 2件目
                jsonPath("$[1].id") { value(2) }
                jsonPath("$[1].title") { value("book2") }
                jsonPath("$[1].price") { value(2000) }
                jsonPath("$[1].status") { value("PUBLISHED") }
                jsonPath("$[1].authorIds[0]") { value(2) }
                jsonPath("$[1].authorIds[1]") { value(3) }
            }
    }

    @Test
    fun `本を登録して返す`() {
        // given
        val requestJson =
            """
            {
              "title": "new book",
              "price": 3000,
              "status": "PUBLISHED",
              "publishedAt": "2024-03-01",
              "authorIds": [1, 2]
            }
            """.trimIndent()

        val createdBook =
            Book(
                id = 10L,
                title = "new book",
                price = 3000,
                status = PublicationStatus.PUBLISHED,
                publishedAt = LocalDate.of(2024, 3, 1),
                authorIds = listOf(1L, 2L),
            )

        // Book 引数は any() でマッチさせる
        @Suppress("UNCHECKED_CAST")
        whenever(bookService.createBook(any())).thenReturn(createdBook)

        // when & then
        mockMvc
            .post("/v1/books") {
                contentType = MediaType.APPLICATION_JSON
                accept = MediaType.APPLICATION_JSON
                content = requestJson
            }.andExpect {
                status { isOk() }

                jsonPath("$.id") { value(10) }
                jsonPath("$.title") { value("new book") }
                jsonPath("$.price") { value(3000) }
                jsonPath("$.status") { value("PUBLISHED") }
                jsonPath("$.authorIds[0]") { value(1) }
                jsonPath("$.authorIds[1]") { value(2) }
            }
    }

    @Test
    fun `本を更新して返す`() {
        // given
        val bookId = 20L

        val requestJson =
            """
            {
              "title": "updated title",
              "price": 4000,
              "status": "UNPUBLISHED",
              "publishedAt": "2024-04-01",
              "authorIds": [3]
            }
            """.trimIndent()

        val updatedBook =
            Book(
                id = bookId,
                title = "updated title",
                price = 4000,
                status = PublicationStatus.UNPUBLISHED,
                publishedAt = LocalDate.of(2024, 4, 1),
                authorIds = listOf(3L),
            )

        @Suppress("UNCHECKED_CAST")
        whenever(bookService.updateBook(eq(bookId), any())).thenReturn(updatedBook)

        // when & then
        mockMvc
            .put("/v1/books/{id}", bookId) {
                contentType = MediaType.APPLICATION_JSON
                accept = MediaType.APPLICATION_JSON
                content = requestJson
            }.andExpect {
                status { isOk() }

                jsonPath("$.id") { value(bookId.toInt()) }
                jsonPath("$.title") { value("updated title") }
                jsonPath("$.price") { value(4000) }
                jsonPath("$.status") { value("UNPUBLISHED") }
                jsonPath("$.authorIds[0]") { value(3) }
            }
    }

    @Test
    fun `PUT 存在しないIDなら404を返す`() {
        val id = 999L

        given(bookService.updateBook(eq(id), any()))
            .willThrow(BookNotFoundException(id))

        val requestBody =
            """
            {
              "title": "updated",
              "price": 1200,
              "status": "PUBLISHED",
              "publishedAt": "2024-01-01",
              "authorIds": [1]
            }
            """.trimIndent()

        mockMvc
            .put("/v1/books/{id}", id) {
                contentType = MediaType.APPLICATION_JSON
                content = requestBody
                accept = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isNotFound() }
                jsonPath("$.status") { value(404) }
                jsonPath("$.error") { value("Not Found") }
                jsonPath("$.message") { value("Book not found. id=$id") }
            }
    }

    @Test
    fun `PUT 出版済み→未出版は409を返す`() {
        val id = 1L

        given(bookService.updateBook(eq(id), any()))
            .willThrow(InvalidBookOperationException("published book cannot be reverted to unpublished"))

        val requestBody =
            """
            {
              "title": "book",
              "price": 1000,
              "status": "UNPUBLISHED",
              "publishedAt": "2024-01-01",
              "authorIds": [1]
            }
            """.trimIndent()

        mockMvc
            .put("/v1/books/{id}", id) {
                contentType = MediaType.APPLICATION_JSON
                content = requestBody
                accept = MediaType.APPLICATION_JSON
            }.andExpect {
                status { isConflict() }
                jsonPath("$.status") { value(409) }
                jsonPath("$.error") { value("Conflict") }
                jsonPath("$.message") { value("published book cannot be reverted to unpublished") }
            }
    }
}
