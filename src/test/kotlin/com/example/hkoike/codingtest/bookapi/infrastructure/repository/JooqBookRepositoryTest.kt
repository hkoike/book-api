package com.example.hkoike.codingtest.bookapi.infrastructure.repository

import com.example.hkoike.codingtest.bookapi.BookApiApplication
import com.example.hkoike.codingtest.bookapi.domain.model.Book
import com.example.hkoike.codingtest.bookapi.domain.model.PublicationStatus
import com.example.hkoike.codingtest.bookapi.domain.repository.BookRepository
import com.example.hkoike.codingtest.bookapi.jooq.tables.Author.AUTHOR
import com.example.hkoike.codingtest.bookapi.jooq.tables.Book.BOOK
import com.example.hkoike.codingtest.bookapi.jooq.tables.BookAuthor.BOOK_AUTHOR
import org.jooq.DSLContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(classes = [BookApiApplication::class])
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class JooqBookRepositoryTest(
    @Autowired private val bookRepository: BookRepository,
    @Autowired private val dsl: DSLContext,
) {
    @BeforeEach
    fun setUp() {
        // テーブルをきれいにしておく（FKの順番に注意）
        dsl.deleteFrom(BOOK_AUTHOR).execute()
        dsl.deleteFrom(BOOK).execute()
        dsl.deleteFrom(AUTHOR).execute()
    }

    private fun insertAuthor(name: String): Long =
        dsl
            .insertInto(AUTHOR)
            .set(AUTHOR.NAME, name)
            .set(AUTHOR.BIRTH_DATE, LocalDate.of(2000, 1, 1))
            .returningResult(AUTHOR.ID)
            .fetchOne()!!
            .get(AUTHOR.ID)!!
            .toLong()

    @Test
    fun `save で book と author の紐付けが保存され findById で取得できる`() {
        val authorId1 = insertAuthor("author1")
        val authorId2 = insertAuthor("author2")

        val book =
            Book(
                id = 0L,
                title = "DDD入門",
                price = 3000,
                status = PublicationStatus.UNPUBLISHED,
                publishedAt = LocalDate.of(2024, 1, 1),
                authorIds = listOf(authorId1, authorId2),
            )

        val saved = bookRepository.save(book)

        assertNotNull(saved.id)
        assertEquals("DDD入門", saved.title)

        val found = bookRepository.findById(saved.id)
        assertNotNull(found)
        assertEquals(saved.id, found!!.id)
        assertEquals(listOf(authorId1, authorId2), found.authorIds)
    }

    @Test
    fun `存在しないIDをfindByIdするとnull`() {
        val result = bookRepository.findById(9999L)
        assertNull(result)
    }

    @Test
    fun `findByAuthorIds は指定した著者の本を返す`() {
        val author1 = insertAuthor("author1")
        val author2 = insertAuthor("author2")
        val author3 = insertAuthor("author3")

        val book1 =
            bookRepository.save(
                Book(
                    id = 0L,
                    title = "book1",
                    price = 1000,
                    status = PublicationStatus.PUBLISHED,
                    publishedAt = LocalDate.of(2024, 1, 1),
                    authorIds = listOf(author1),
                ),
            )

        val book2 =
            bookRepository.save(
                Book(
                    id = 0L,
                    title = "book2",
                    price = 2000,
                    status = PublicationStatus.PUBLISHED,
                    publishedAt = LocalDate.of(2024, 2, 1),
                    authorIds = listOf(author2, author3),
                ),
            )

        // author2 で検索すると book2 が返る
        val result = bookRepository.findByAuthorIds(listOf(author2))

        assertEquals(1, result.size)
        assertEquals(book2.id, result[0].id)
        assertEquals(listOf(author2, author3), result[0].authorIds)
    }
}
