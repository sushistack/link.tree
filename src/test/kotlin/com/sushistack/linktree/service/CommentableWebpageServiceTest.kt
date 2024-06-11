package com.sushistack.linktree.service

import com.sushistack.linktree.entity.publisher.Comment
import com.sushistack.linktree.repository.publisher.CommentRepository
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
class CommentServiceTest {


    @Autowired
    lateinit var commentService: CommentService

    @Autowired
    lateinit var commentRepository: CommentRepository

    @DisplayName("find comment with limit size")
    @ParameterizedTest(name = "[{index}] comments size = {0}, limit = {1}, expected size = {2}")
    @MethodSource("commentsProvider")
    fun findByOrderByUsedCountLimit(commentSize: Long, limit: Long, expectedSize: Int) {
        // Given
        val comments = (1..commentSize).map { Comment() }
        commentRepository.saveAll(comments)

        // Then
        val savedComments = commentService.findByOrderByUsedCountLimit(limit)
        Assertions.assertThat(savedComments).hasSize(expectedSize)
    }

    companion object {
        @JvmStatic
        fun commentsProvider() = listOf(
            Arguments.of(2, 3, 2),
            Arguments.of(10, 3, 3)
        )
    }
}