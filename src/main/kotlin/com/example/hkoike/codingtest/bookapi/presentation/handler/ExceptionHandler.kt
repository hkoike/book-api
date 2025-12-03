package com.example.hkoike.codingtest.bookapi.presentation.handler

import com.example.hkoike.codingtest.bookapi.domain.exception.BookNotFoundException
import com.example.hkoike.codingtest.bookapi.domain.exception.InvalidBookOperationException
import com.example.hkoike.codingtest.bookapi.presentation.dto.ErrorResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(BookNotFoundException::class)
    fun handleBookNotFound(ex: BookNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponse(
                    status = HttpStatus.NOT_FOUND.value(),
                    error = HttpStatus.NOT_FOUND.reasonPhrase,
                    message = ex.message,
                ),
            )

    @ExceptionHandler(InvalidBookOperationException::class)
    fun handleInvalidBookOperation(ex: InvalidBookOperationException): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.CONFLICT) // 409
            .body(
                ErrorResponse(
                    status = HttpStatus.CONFLICT.value(),
                    error = HttpStatus.CONFLICT.reasonPhrase,
                    message = ex.message,
                ),
            )

    @ExceptionHandler(IllegalArgumentException::class, MethodArgumentNotValidException::class)
    fun handleBadRequest(ex: Exception): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    status = HttpStatus.BAD_REQUEST.value(),
                    error = HttpStatus.BAD_REQUEST.reasonPhrase,
                    message = ex.message,
                ),
            )
}
