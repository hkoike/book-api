package com.example.hkoike.codingtest.bookapi.domain.exception

class BookNotFoundException(
    val bookId: Long,
) : RuntimeException("Book not found. id=$bookId")
