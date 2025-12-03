package com.example.hkoike.codingtest.bookapi.presentation.dto

import com.example.hkoike.codingtest.bookapi.domain.model.PublicationStatus
import java.time.LocalDate

data class BookResponse(
    val id: Long,
    val title: String,
    val price: Int,
    val status: PublicationStatus,
    val publishedAt: LocalDate?,
    val authorIds: List<Long>,
)
