package com.example.hkoike.codingtest.bookapi.presentation.dto

import com.example.hkoike.codingtest.bookapi.domain.model.PublicationStatus
import java.time.LocalDate

/**
 * Book 登録・更新用のリクエスト DTO
 */
data class BookRequest(
    val title: String,
    val price: Int,
    val status: PublicationStatus,
    val publishedAt: LocalDate?,
    val authorIds: List<Long>,
)
