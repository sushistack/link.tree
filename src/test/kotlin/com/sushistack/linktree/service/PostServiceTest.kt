package com.sushistack.linktree.service

import com.sushistack.linktree.entity.content.Post
import com.sushistack.linktree.repository.content.PostRepository
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
class PostServiceTest {

    @Autowired
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var postService: PostService

    @Autowired
    lateinit var postRepository: PostRepository

    @Test
    fun createPost() {
        // Given
        val post = Post()
        postService.createPost(post)
        entityManager.flush()
        entityManager.clear()

        // Then
        val posts = postRepository.findAll()
        Assertions.assertThat(posts.size).isEqualTo(1)
    }
}