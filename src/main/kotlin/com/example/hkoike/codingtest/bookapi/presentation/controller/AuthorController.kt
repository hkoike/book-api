package com.example.hkoike.codingtest.bookapi.presentation.controller

import com.example.hkoike.codingtest.bookapi.application.service.AuthorService
import com.example.hkoike.codingtest.bookapi.presentation.dto.AuthorRequest
import com.example.hkoike.codingtest.bookapi.presentation.dto.AuthorResponse
import com.example.hkoike.codingtest.bookapi.presentation.mapper.AuthorMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/authors")
class AuthorController(
    private val authorService: AuthorService,
) {

    @PostMapping
    fun createAuthor(
        @RequestBody request: AuthorRequest,
    ): ResponseEntity<AuthorResponse> {
        val author = AuthorMapper.toAuthor(request)
        val saved = authorService.createAuthor(author)
        return ResponseEntity.status(HttpStatus.OK)
            .body(AuthorMapper.toResponse(saved))
    }

    @PutMapping("/{id}")
    fun updateAuthor(
        @PathVariable id: Long,
        @RequestBody request: AuthorRequest,
    ): ResponseEntity<AuthorResponse> {
        val author = AuthorMapper.toAuthor(request, id)
        val updated = authorService.updateAuthor(id, author)
        return ResponseEntity.ok(AuthorMapper.toResponse(updated))
    }
}
