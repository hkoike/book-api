package com.example.hkoike.codingtest.bookapi.presentation.dto

import java.time.LocalDate

data class AuthorRequest (
    val name: String,
    val birthDate: LocalDate,
)