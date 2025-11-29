package com.example.hkoike.codingtest.bookapi.domain.model

import java.time.LocalDate

data class Book(
    val id: Long,
    val title: String,
    val price: Int,
    val status: PublicationStatus,
    val publishedAt: LocalDate,
    val authorIds: List<Long>,
)
