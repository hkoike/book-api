package com.example.hkoike.codingtest.bookapi.presentation.controller

import com.example.hkoike.codingtest.bookapi.application.service.BookService
import com.example.hkoike.codingtest.bookapi.domain.model.Book
import com.example.hkoike.codingtest.bookapi.domain.model.PublicationStatus
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.LocalDate
import org.mockito.BDDMockito.given

@WebMvcTest(BookController::class)
class BookControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var bookService: BookService

    @Test
    fun `authorIdsを指定して紐づく本を返す`() {
        val authorIds = listOf(1L, 2L)

        val books = listOf(
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

        given(bookService.getBooksByAuthors(authorIds)).willReturn(books)

        mockMvc.get("/v1/books") {
            param("authorIds", "1", "2")   // ?authorIds=1&authorIds=2
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.length()") { value(2) }
                jsonPath("$[0].id") { value(1) }
                jsonPath("$[0].title") { value("book1") }
                jsonPath("$[0].price") { value(1000) }
                jsonPath("$[0].status") { value("UNPUBLISHED") }
                jsonPath("$[0].authorIds[0]") { value(1) }

                jsonPath("$[1].id") { value(2) }
                jsonPath("$[1].status") { value("PUBLISHED") }
                jsonPath("$[1].authorIds[0]") { value(2) }
                jsonPath("$[1].authorIds[1]") { value(3) }
            }
    }
}
