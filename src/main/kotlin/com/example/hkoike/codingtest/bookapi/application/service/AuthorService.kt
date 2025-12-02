package com.example.hkoike.codingtest.bookapi.application.service

import com.example.hkoike.codingtest.bookapi.domain.model.Author
import com.example.hkoike.codingtest.bookapi.domain.repository.AuthorRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AuthorService(
    private val authorRepository: AuthorRepository,
) {

    fun createAuthor(author: Author): Author {
        validateAuthor(author)

        return authorRepository.save(author)
    }

    fun updateAuthor(id: Long, author: Author): Author {
        validateAuthor(author)

        val existing =
            authorRepository.findById(id)
                ?: throw NoSuchElementException("id=$id の著者が存在しません")

        // 必要なら existing を見て差分制御もできるが、ここでは丸ごと上書き
        val toSave =
            existing.copy(
                name = author.name,
                birthDate = author.birthDate,
            )

        return authorRepository.save(toSave)
    }

    private fun validateAuthor(author: Author) {
        require(author.birthDate < LocalDate.now()) { "birthDate must not be in the future" }
    }
}
