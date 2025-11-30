package com.example.hkoike.codingtest.bookapi.application.service

import com.example.hkoike.codingtest.bookapi.domain.model.Book
import com.example.hkoike.codingtest.bookapi.domain.model.PublicationStatus
import com.example.hkoike.codingtest.bookapi.domain.repository.BookRepository
import org.springframework.stereotype.Service

@Service
class BookService(
    private val bookRepository: BookRepository,
) {

    fun createBook(book: Book): Book {
        validateBook(book)

        return bookRepository.save(book)
    }

    fun updateBook(id: Long, request: Book): Book {
        val existing = bookRepository.findById(id)
                        ?: throw IllegalArgumentException("Book not found: $id")

        validateBook(request)
        validateStatusChange(existing.status, request.status)

        val toSave = request.copy(id = existing.id)

        return bookRepository.save(toSave)
    }

    private fun validateBook(book: Book) {
        require(book.price >= 0) { "price must be over 0" }
        require(book.authorIds.isNotEmpty()) { "book must have at least one author" }
    }

    private fun validateStatusChange(
        current: PublicationStatus,
        next: PublicationStatus,
    ) {
        if (current == PublicationStatus.PUBLISHED && next == PublicationStatus.UNPUBLISHED) {
            throw IllegalStateException("PUBLISHED book cannot be reverted to UNPUBLISHED")
        }
    }
}
