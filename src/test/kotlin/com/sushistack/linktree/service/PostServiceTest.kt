package com.sushistack.linktree.service

import com.sushistack.linktree.entity.content.Post
import com.sushistack.linktree.entity.git.GitAccount
import com.sushistack.linktree.entity.git.GitRepository
import com.sushistack.linktree.entity.publisher.StaticWebpage
import com.sushistack.linktree.model.ArticleSource
import com.sushistack.linktree.repository.content.PostRepository
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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

    @Value("\${spring.application.name}")
    private lateinit var appName: String

    @Value("\${test.bitbucket.username}")
    private lateinit var bitbucketUsername: String

    @Value("\${test.bitbucket.app-password}")
    private lateinit var bitbucketAppPassword: String

    private lateinit var homeDir: String

    private lateinit var repositoryName: String

    @Test
    fun createPost() {
        // Given
        val gitAccount = GitAccount(username = bitbucketUsername, appPassword = bitbucketAppPassword)
        entityManager.persist(gitAccount)
        val webpage = StaticWebpage()
        entityManager.persist(webpage)
        val repository = GitRepository(workspaceName = bitbucketUsername, repositoryName = "playground", gitAccount = gitAccount)
        entityManager.persist(repository)
        val post = Post(filePath = "life/test.md", webpage = webpage)
        entityManager.persist(post)
        postService.createPost(post, listOf(ArticleSource("files/articles/감자의 효능/0.json")))
        entityManager.flush()
        entityManager.clear()

        // Then
        val posts = postRepository.findAll()
        Assertions.assertThat(posts.size).isEqualTo(1)
    }
}