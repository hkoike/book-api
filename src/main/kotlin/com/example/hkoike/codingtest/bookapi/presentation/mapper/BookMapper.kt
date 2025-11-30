package com.example.hkoike.codingtest.bookapi.presentation.mapper

import com.example.hkoike.codingtest.bookapi.domain.model.Book
import com.example.hkoike.codingtest.bookapi.presentation.dto.BookResponse

object BookMapper {
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