package com.example.hkoike.codingtest.book_api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration
import org.springframework.boot.runApplication

// todo: 一時的にDatasourceを無効化
@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
// @SpringBootApplication
class BookApiApplication

fun main(args: Array<String>) {
	runApplication<BookApiApplication>(*args)
}
