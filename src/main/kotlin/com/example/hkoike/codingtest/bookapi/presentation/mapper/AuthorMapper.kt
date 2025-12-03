package com.example.hkoike.codingtest.bookapi.presentation.mapper

import com.example.hkoike.codingtest.bookapi.domain.model.Author
import com.example.hkoike.codingtest.bookapi.presentation.dto.AuthorRequest
import com.example.hkoike.codingtest.bookapi.presentation.dto.AuthorResponse

object AuthorMapper {
    fun toAuthor(
        request: AuthorRequest,
        id: Long = 0L,
    ): Author =
        Author(
            id = id,
            name = request.name,
            birthDate = request.birthDate,
        )

    fun toResponse(author: Author): AuthorResponse =
        AuthorResponse(
            id = author.id,
            name = author.name,
            birthDate = author.birthDate,
        )
}
