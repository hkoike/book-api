package com.example.hkoike.codingtest.bookapi.presentation.controller

import com.example.hkoike.codingtest.bookapi.application.service.BookService
import com.example.hkoike.codingtest.bookapi.presentation.dto.BookRequest
import com.example.hkoike.codingtest.bookapi.presentation.dto.BookResponse
import com.example.hkoike.codingtest.bookapi.presentation.dto.toBook
import com.example.hkoike.codingtest.bookapi.presentation.mapper.BookMapper.toResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/books")
class BookController(
    private val bookService: BookService,
) {

    @GetMapping
    fun getBooksByAuthors(
        @RequestParam authorIds: List<Long>,
    ): List<BookResponse> {
        val books = bookService.getBooksByAuthors(authorIds)

        return books.map { it.toResponse() }
    }

    @PostMapping
    fun postBook(@RequestBody request: BookRequest): ResponseEntity<BookResponse> {
        val book = bookService.createBook(request.toBook())

        return ResponseEntity.ok(book.toResponse())
    }

    @PutMapping("/{id}")
    fun putBook(
        @PathVariable id: Long,
        @RequestBody request: BookRequest
    ): ResponseEntity<BookResponse> {
        val book = bookService.updateBook(id, request.toBook())

        return ResponseEntity.ok(book.toResponse())
    }
}
