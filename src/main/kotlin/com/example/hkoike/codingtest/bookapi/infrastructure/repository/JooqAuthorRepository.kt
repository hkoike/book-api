package com.example.hkoike.codingtest.bookapi.infrastructure.repository

import com.example.hkoike.codingtest.bookapi.domain.model.Author
import com.example.hkoike.codingtest.bookapi.domain.repository.AuthorRepository
import com.example.hkoike.codingtest.bookapi.jooq.tables.Author.AUTHOR
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository

@Repository
class JooqAuthorRepository(
    private val dsl: DSLContext,
) : AuthorRepository {
    override fun save(author: Author): Author =
        if (author.id == 0L) {
            insert(author)
        } else {
            update(author)
        }

    override fun findById(id: Long): Author? {
        val record =
            dsl
                .selectFrom(AUTHOR)
                .where(AUTHOR.ID.eq(id))
                .fetchOne() ?: return null

        return Author(
            id = record.id!!.toLong(),
            name = record.name!!,
            birthDate = record.birthDate!!,
        )
    }

    // ------------------------
    // private helpers
    // ------------------------

    private fun insert(author: Author): Author =
        dsl.transactionResult { cfg ->
            val tx = DSL.using(cfg)

            val id =
                tx
                    .insertInto(AUTHOR)
                    .set(AUTHOR.NAME, author.name)
                    .set(AUTHOR.BIRTH_DATE, author.birthDate)
                    .returningResult(AUTHOR.ID)
                    .fetchOne()!!
                    .get(AUTHOR.ID)!!
                    .toLong()

            author.copy(id = id)
        }

    private fun update(author: Author): Author =
        dsl.transactionResult { cfg ->
            val tx = DSL.using(cfg)

            tx
                .update(AUTHOR)
                .set(AUTHOR.NAME, author.name)
                .set(AUTHOR.BIRTH_DATE, author.birthDate)
                .where(AUTHOR.ID.eq(author.id))
                .execute()

            author
        }
}
