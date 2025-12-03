package com.example.hkoike.codingtest.bookapi.presentation.mapper

import com.example.hkoike.codingtest.bookapi.domain.model.Book
import com.example.hkoike.codingtest.bookapi.domain.model.PublicationStatus
import com.example.hkoike.codingtest.bookapi.presentation.dto.BookRequest
import com.example.hkoike.codingtest.bookapi.presentation.dto.BookResponse
import java.time.LocalDate

object BookMapper {
    fun BookRequest.toBook(id: Long = 0L): Book =
        Book(
            id = id,
            title = title,
            price = price,
            status = status,
            publishedAt =
                if (publishedAt == null && status == PublicationStatus.PUBLISHED) {
                    LocalDate.now()
                } else {
                    publishedAt
                },
            authorIds = authorIds,
        )

    fun Book.toResponse(): BookResponse =
        BookResponse(
            id = id,
            title = title,
            price = price,
            status = status,
            publishedAt = publishedAt,
            authorIds = authorIds,
        )
}
