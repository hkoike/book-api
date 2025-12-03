package com.example.hkoike.codingtest.bookapi.presentation.dto

data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String?,
)
