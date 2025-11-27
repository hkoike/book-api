package com.example.hkoike.codingtest.bookapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration
import org.springframework.boot.runApplication

// todo: 一時的にDatasourceを無効化
// @SpringBootApplication
@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
class BookApiApplication

fun main(args: Array<String>) {
    runApplication<BookApiApplication>(*args)
}
