package com.example.hkoike.codingtest.bookapi.application.service

import com.example.hkoike.codingtest.bookapi.domain.exception.InvalidBookOperationException
import com.example.hkoike.codingtest.bookapi.domain.model.Book
import com.example.hkoike.codingtest.bookapi.domain.model.PublicationStatus
import com.example.hkoike.codingtest.bookapi.domain.repository.BookRepository
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertTrue

class BookServiceTest {

    private val bookRepository: BookRepository = mockk()
    private val bookService = BookService(bookRepository)

    @Nested
    inner class CreateBook {
        @ParameterizedTest
        @ValueSource(ints = [0, 1, 1000])
        fun `正常な場合は作成される`(price: Int) {
            val book = Book(
                id = 0L,
                title = "valid book",
                price = price,
                status = PublicationStatus.UNPUBLISHED,
                publishedAt = LocalDate.now(),
                authorIds = listOf(1L),
            )

            val saved = book.copy(id = 10L)
            every { bookRepository.save(any()) } returns saved

            val result = bookService.createBook(book)
            assertEquals(10L, result.id)
            assertEquals("valid book", result.title)
        }

        @ParameterizedTest
        @ValueSource(ints = [-999, -1])
        fun `priceがマイナスの場合は例外を投げる`(price: Int) {
            val book = Book(
                id = 0L,
                title = "test",
                price = price,
                status = PublicationStatus.UNPUBLISHED,
                publishedAt = LocalDate.now(),
                authorIds = listOf(1L),
            )

            assertThrows(IllegalArgumentException::class.java) {
                bookService.createBook(book)
            }
        }

        @Test
        fun `authorが0の場合は例外を投げる`() {
            val book = Book(
                id = 0L,
                title = "no author",
                price = 1000,
                status = PublicationStatus.UNPUBLISHED,
                publishedAt = LocalDate.now(),
                authorIds = emptyList(),
            )

            assertThrows(IllegalArgumentException::class.java) {
                bookService.createBook(book)
            }
        }
    }

    @Nested
    inner class ChangeStatus {
        @Test
        fun `未出版から出版済みへの変更は許可される`() {
            val existing = Book(
                id = 1L,
                title = "draft",
                price = 1000,
                status = PublicationStatus.UNPUBLISHED,
                publishedAt = LocalDate.now(),
                authorIds = listOf(1L),
            )
            every { bookRepository.findById(1L) } returns existing

            val updateRequest = existing.copy(status = PublicationStatus.PUBLISHED)
            val updated = updateRequest
            every { bookRepository.save(updateRequest) } returns updated

            val result = bookService.updateBook(1L, updateRequest)
            assertEquals(PublicationStatus.PUBLISHED, result.status)
        }

        @Test
        fun `出版済みの本を未出版に戻そうとすると例外`() {
            val existing = Book(
                id = 2L,
                title = "published",
                price = 1000,
                status = PublicationStatus.PUBLISHED,
                publishedAt = LocalDate.now(),
                authorIds = listOf(1L),
            )

            every { bookRepository.findById(2L) } returns existing

            val updateRequest = existing.copy(status = PublicationStatus.UNPUBLISHED)
            assertThrows(InvalidBookOperationException::class.java) {
                bookService.updateBook(2L, updateRequest)
            }
        }
    }

    @Nested
    inner class GetBooksByAuthors {
        @Test
        fun `authorsに紐づく本が複数存在する場合は全て返す`() {
            val authorIds = listOf(1L, 2L)

            val books = listOf(
                Book(
                    id = 1L,
                    title = "book1",
                    price = 1000,
                    status = PublicationStatus.UNPUBLISHED,
                    publishedAt = LocalDate.now(),
                    authorIds = listOf(1L),
                ),
                Book(
                    id = 2L,
                    title = "book2",
                    price = 2000,
                    status = PublicationStatus.PUBLISHED,
                    publishedAt = LocalDate.now(),
                    authorIds = listOf(2L, 3L),
                ),
            )

            every { bookRepository.findByAuthorIds(authorIds) } returns books

            val result = bookService.getBooksByAuthors(authorIds)

            assertEquals(2, result.size)
            assertEquals(listOf(1L, 2L), listOf(result[0].id, result[1].id))
        }

        @Test
        fun `著者に紐づく本が存在しない場合は空リストを返す`() {
            val authorIds = listOf(99L, 100L)
            every { bookRepository.findByAuthorIds(authorIds) } returns emptyList()

            val result = bookService.getBooksByAuthors(authorIds)

            assertTrue(result.isEmpty())
        }

        @Test
        fun `authorIdsが空の場合は例外を投げる`() {
            val authorIds = emptyList<Long>()

            assertThrows(IllegalArgumentException::class.java) {
                bookService.getBooksByAuthors(authorIds)
            }
        }

        @ParameterizedTest
        @ValueSource(longs = [0L, -1L, -10L])
        fun `authorIdsに0以下が含まれる場合は例外を投げる`(invalidId: Long) {
            val authorIds = listOf(1L, invalidId, 2L)

            assertThrows(IllegalArgumentException::class.java) {
                bookService.getBooksByAuthors(authorIds)
            }
        }
    }
}
