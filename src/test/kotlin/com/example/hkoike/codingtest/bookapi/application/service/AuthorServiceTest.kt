package com.example.hkoike.codingtest.bookapi.application.service

import com.example.hkoike.codingtest.bookapi.domain.model.Author
import com.example.hkoike.codingtest.bookapi.domain.repository.AuthorRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate

class AuthorServiceTest {
    private val authorRepository: AuthorRepository = mockk()
    private val authorService = AuthorService(authorRepository)

    @Nested
    inner class CreateAuthor {
        @Test
        fun `正常に作成できる`() {
            val input =
                Author(
                    id = 0L,
                    name = "author1",
                    birthDate = LocalDate.now().minusYears(20),
                )

            val saved = input.copy(id = 10L)

            every { authorRepository.save(input) } returns saved

            val result = authorService.createAuthor(input)

            assertEquals(10L, result.id)
            assertEquals("author1", result.name)
        }

        @Test
        fun `生年月日が未来日だと例外`() {
            val future = LocalDate.now().plusDays(1)
            val author =
                Author(
                    id = 0L,
                    name = "author",
                    birthDate = future,
                )

            assertThrows(IllegalArgumentException::class.java) {
                authorService.createAuthor(author)
            }
            assertThrows(IllegalArgumentException::class.java) {
                authorService.updateAuthor(id = 1L, author)
            }
        }
    }

    @Nested
    inner class UpdateAuthor {
        @Test
        fun `著者を更新できる`() {
            val existing =
                Author(
                    id = 1L,
                    name = "old name",
                    birthDate = LocalDate.of(1990, 1, 1),
                )

            val updateInput =
                Author(
                    id = 1L,
                    name = "new name",
                    birthDate = LocalDate.of(1991, 2, 2),
                )

            every { authorRepository.findById(1L) } returns existing
            every {
                authorRepository.save(
                    existing.copy(
                        name = updateInput.name,
                        birthDate = updateInput.birthDate,
                    ),
                )
            } returns updateInput

            val result = authorService.updateAuthor(1L, updateInput)

            assertEquals(1L, result.id)
            assertEquals("new name", result.name)
            assertEquals(LocalDate.of(1991, 2, 2), result.birthDate)
        }

        @Test
        fun `存在しないIDを更新しようとすると例外`() {
            val updateInput =
                Author(
                    id = 999L,
                    name = "any",
                    birthDate = LocalDate.of(1990, 1, 1),
                )

            every { authorRepository.findById(999L) } returns null

            assertThrows(NoSuchElementException::class.java) {
                authorService.updateAuthor(999L, updateInput)
            }
        }
    }
}
