package com.example.hkoike.codingtest.bookapi.infrastructure.repository

import com.example.hkoike.codingtest.bookapi.domain.model.Author
import com.example.hkoike.codingtest.bookapi.domain.repository.AuthorRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@SpringBootTest
@Transactional
class JooqAuthorRepositoryTest(

    @Autowired
    private val authorRepository: AuthorRepository,
) {

    @Test
    fun `save で著者を新規登録し findById で取得できる`() {
        val author =
            Author(
                id = 0L,
                name = "author1",
                birthDate = LocalDate.of(1990, 1, 1),
            )

        val saved = authorRepository.save(author)

        val found = authorRepository.findById(saved.id)!!

        assertEquals(saved.id, found.id)
        assertEquals("author1", found.name)
        assertEquals(LocalDate.of(1990, 1, 1), found.birthDate)
    }

    @Test
    fun `save で既存著者を更新できる`() {
        val author =
            Author(
                id = 0L,
                name = "author1",
                birthDate = LocalDate.of(1990, 1, 1),
            )

        val saved = authorRepository.save(author)

        val updatedInput =
            Author(
                id = saved.id,
                name = "updated",
                birthDate = LocalDate.of(1980, 5, 5),
            )

        val updated = authorRepository.save(updatedInput)

        val found = authorRepository.findById(saved.id)!!

        assertEquals(saved.id, updated.id)
        assertEquals("updated", found.name)
        assertEquals(LocalDate.of(1980, 5, 5), found.birthDate)
    }

    @Test
    fun `存在しないIDをfindByIdするとnull`() {
        val result = authorRepository.findById(999999L)
        assertNull(result)
    }
}
