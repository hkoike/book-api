package com.example.hkoike.codingtest.bookapi.infrastructure.repository

import com.example.hkoike.codingtest.bookapi.domain.model.Book
import com.example.hkoike.codingtest.bookapi.domain.model.PublicationStatus
import com.example.hkoike.codingtest.bookapi.domain.repository.BookRepository
import com.example.hkoike.codingtest.bookapi.jooq.tables.Author.AUTHOR
import com.example.hkoike.codingtest.bookapi.jooq.tables.Book.BOOK
import com.example.hkoike.codingtest.bookapi.jooq.tables.BookAuthor.BOOK_AUTHOR
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class JooqBookRepository(
    private val dsl: DSLContext,
) : BookRepository {

    override fun save(book: Book): Book {
        return if (book.id == 0L) {
            insert(book)
        } else {
            update(book)
        }
    }

    override fun findById(id: Long): Book? {
        // book と book_author を JOIN して 1冊＋複数著者 を取得
        val records =
            dsl
                .select(
                    BOOK.ID,
                    BOOK.TITLE,
                    BOOK.PRICE,
                    BOOK.STATUS,
                    BOOK.PUBLISHED_AT,
                    BOOK_AUTHOR.AUTHOR_ID,
                ).from(BOOK)
                .leftJoin(BOOK_AUTHOR)
                .on(BOOK.ID.eq(BOOK_AUTHOR.BOOK_ID))
                .where(BOOK.ID.eq(id))
                .fetch()

        if (records.isEmpty()) return null

        val first = records[0]

        val authorIds =
            records
                .mapNotNull { it.get(BOOK_AUTHOR.AUTHOR_ID) }
                .map { it.toLong() }

        return Book(
            id = first.get(BOOK.ID)!!.toLong(),
            title = first.get(BOOK.TITLE)!!,
            price = first.get(BOOK.PRICE)!!,
            status = PublicationStatus.valueOf(first.get(BOOK.STATUS)!!),
            publishedAt = first.get(BOOK.PUBLISHED_AT) as LocalDate?,
            authorIds = authorIds,
        )
    }

    override fun findByAuthorIds(authorIds: List<Long>): List<Book> {
        if (authorIds.isEmpty()) return emptyList()

        // 指定された authorIds を持つ book_id を取得
        val bookIds =
            dsl
                .selectDistinct(BOOK_AUTHOR.BOOK_ID)
                .from(BOOK_AUTHOR)
                .where(BOOK_AUTHOR.AUTHOR_ID.`in`(authorIds))
                .fetch(BOOK_AUTHOR.BOOK_ID)
                .map { it.toLong() }

        if (bookIds.isEmpty()) return emptyList()

        // book_id について、全ての author を含めて取り直す
        val records =
            dsl
                .select(
                    BOOK.ID,
                    BOOK.TITLE,
                    BOOK.PRICE,
                    BOOK.STATUS,
                    BOOK.PUBLISHED_AT,
                    BOOK_AUTHOR.AUTHOR_ID,
                ).from(BOOK)
                .join(BOOK_AUTHOR)
                .on(BOOK.ID.eq(BOOK_AUTHOR.BOOK_ID))
                .where(BOOK.ID.`in`(bookIds))
                .orderBy(BOOK.ID.asc(), BOOK_AUTHOR.AUTHOR_ID.asc())
                .fetch()

        if (records.isEmpty()) return emptyList()

        // book.id ごとに groupBy して、authorIds をまとめる
        return records
            .groupBy(
                { it.get(BOOK.ID)!!.toLong() },
                { it },
            ).values
            .map { group ->
                val first = group[0]
                val ids =
                    group
                        .mapNotNull { it.get(BOOK_AUTHOR.AUTHOR_ID) }
                        .map { it.toLong() }

                Book(
                    id = first.get(BOOK.ID)!!.toLong(),
                    title = first.get(BOOK.TITLE)!!,
                    price = first.get(BOOK.PRICE)!!,
                    status = PublicationStatus.valueOf(first.get(BOOK.STATUS)!!),
                    publishedAt = first.get(BOOK.PUBLISHED_AT) as LocalDate?,
                    authorIds = ids,
                )
            }
    }

    // ------------------------
    // private helpers
    // ------------------------

    private fun insert(book: Book): Book =
        dsl.transactionResult { cfg ->
            val tx = DSL.using(cfg)

            // book を insert
            val bookId =
                tx.insertInto(BOOK)
                    .set(BOOK.TITLE, book.title)
                    .set(BOOK.PRICE, book.price)
                    .set(BOOK.STATUS, book.status.name)
                    .set(BOOK.PUBLISHED_AT, book.publishedAt)
                    .returningResult(BOOK.ID)
                    .fetchOne()!!
                    .get(BOOK.ID)!!
                    .toLong()

            // 中間テーブルに authorIds を登録
            if (book.authorIds.isNotEmpty()) {
                tx.batch(
                    book.authorIds.map { authorId ->
                        tx.insertInto(BOOK_AUTHOR)
                            .set(BOOK_AUTHOR.BOOK_ID, bookId)
                            .set(BOOK_AUTHOR.AUTHOR_ID, authorId)
                    },
                ).execute()
            }

            book.copy(id = bookId)
        }

    private fun update(book: Book): Book =
        dsl.transactionResult { cfg ->
            val tx = DSL.using(cfg)

            // book 本体を更新
            tx.update(BOOK)
                .set(BOOK.TITLE, book.title)
                .set(BOOK.PRICE, book.price)
                .set(BOOK.STATUS, book.status.name)
                .set(BOOK.PUBLISHED_AT, book.publishedAt)
                .where(BOOK.ID.eq(book.id))
                .execute()

            // 既存の関連を削除して、authorIds を差し替え
            tx.deleteFrom(BOOK_AUTHOR)
                .where(BOOK_AUTHOR.BOOK_ID.eq(book.id))
                .execute()

            if (book.authorIds.isNotEmpty()) {
                tx.batch(
                    book.authorIds.map { authorId ->
                        tx.insertInto(BOOK_AUTHOR)
                            .set(BOOK_AUTHOR.BOOK_ID, book.id)
                            .set(BOOK_AUTHOR.AUTHOR_ID, authorId)
                    },
                ).execute()
            }

            book
        }
}
