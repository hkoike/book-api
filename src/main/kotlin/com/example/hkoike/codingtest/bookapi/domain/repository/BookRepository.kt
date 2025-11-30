package com.example.hkoike.codingtest.bookapi.domain.repository

import com.example.hkoike.codingtest.bookapi.domain.model.Book

interface BookRepository {
    fun save(book: Book): Book
    fun findById(id: Long): Book?
    fun findByAuthorIds(authorIds: List<Long>): List<Book>
}
