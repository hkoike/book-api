package com.example.hkoike.codingtest.bookapi.domain.model

import java.time.LocalDate

data class Author(
    val id: Long,
    val name: String,
    val birthDate: LocalDate,
)
