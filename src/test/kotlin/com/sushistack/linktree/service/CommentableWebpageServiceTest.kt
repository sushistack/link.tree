package com.sushistack.linktree.service

import com.sushistack.linktree.entity.publisher.CommentableWebpage
import com.sushistack.linktree.repository.publisher.CommentableWebpageRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
class CommentableWebpageServiceTest {


    @Autowired
    lateinit var commentableWebpageService: CommentableWebpageService

    @Autowired
    lateinit var commentableWebpageRepository: CommentableWebpageRepository

    @DisplayName("find comment with limit size")
    @ParameterizedTest(name = "[{index}] comments size = {0}, limit = {1}, expected size = {2}")
    @MethodSource("commentableWebpagesProvider")
    fun findByOrderByUsedCountLimit(commentSize: Long, limit: Long, expectedSize: Int) {
        // Given
        val commentableWebpages = (1..commentSize).map { CommentableWebpage() }
        commentableWebpageRepository.saveAll(commentableWebpages)

        // Then
        val savedCommentableWebpages = commentableWebpageService.findBySeed(1L, limit.toInt())
        Assertions.assertThat(savedCommentableWebpages).hasSize(expectedSize)
    }

    companion object {
        @JvmStatic
        fun commentableWebpagesProvider() = listOf(
            Arguments.of(2, 3, 2),
            Arguments.of(10, 3, 3)
        )
    }
}