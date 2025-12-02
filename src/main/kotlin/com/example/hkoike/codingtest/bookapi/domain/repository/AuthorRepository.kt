package com.example.hkoike.codingtest.bookapi.domain.repository

import com.example.hkoike.codingtest.bookapi.domain.model.Author

interface AuthorRepository {
    fun save(author: Author): Author
    fun findById(id: Long): Author?
}
