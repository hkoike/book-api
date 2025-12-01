package com.example.hkoike.codingtest.bookapi.presentation.dto

import com.example.hkoike.codingtest.bookapi.domain.model.Book
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

/**
 * ドメインモデルへの変換
 * - create のとき   : id = 0L 固定
 * - update のとき   : id = path から渡された値を設定
 */
fun BookRequest.toBook(id: Long = 0L): Book =
    Book(
        id = id,
        title = title,
        price = price,
        status = status,
        publishedAt = publishedAt,
        authorIds = authorIds,
    )
