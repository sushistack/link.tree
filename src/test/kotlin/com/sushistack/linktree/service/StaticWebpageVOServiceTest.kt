package com.sushistack.linktree.service

import com.sushistack.linktree.entity.git.GitAccount
import com.sushistack.linktree.entity.git.GitRepository
import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.entity.publisher.StaticWebpage
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
class StaticWebpageVOServiceTest {
    private val log = KotlinLogging.logger {}

    @Autowired
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var staticWebpageService: StaticWebpageService

    @DisplayName("find static webpage with limit size")
    @ParameterizedTest(name = "[{index}] webpage size = {0}, limit = {1}, expected size = {2}")
    @MethodSource("staticWebpagesProvider")
    fun findStaticWebpages(webpageSize: Long, fixedSize: Int, expectedSize: Int) {
        // Given
        val account = GitAccount()
        entityManager.persist(account)

        val staticWebpages = (1..webpageSize).map {
            StaticWebpage(
                domain = "",
                providerType = ServiceProviderType.PRIVATE_BLOG_NETWORK,
                repository = GitRepository(workspaceName = "", repositoryName = "", webpage = null, gitAccount = account),
                usedCount = 0
            )
        }
        staticWebpages.forEach { web ->
            entityManager.persist(web.repository)
            entityManager.persist(web)
        }
        entityManager.flush()
        entityManager.clear()

        val webpages = staticWebpageService.findStaticWebpagesByProviderType(ServiceProviderType.PRIVATE_BLOG_NETWORK, 1L, fixedSize)
        Assertions.assertThat(webpages).hasSize(expectedSize)
    }

    @Test
    fun consistencyTest() {
        // Given
        val seed = 11L
        val account = GitAccount()
        entityManager.persist(account)

        val staticWebpages = (1..100).map {
            StaticWebpage(
                domain = "",
                providerType = ServiceProviderType.PRIVATE_BLOG_NETWORK,
                repository = GitRepository(workspaceName = "", repositoryName = "", webpage = null, gitAccount = account),
                usedCount = 0
            )
        }
        staticWebpages.forEach { web ->
            entityManager.persist(web.repository)
            entityManager.persist(web)
        }
        entityManager.flush()
        entityManager.clear()

        // When

        // Then
        val expected = staticWebpageService.findStaticWebpagesByProviderType(ServiceProviderType.PRIVATE_BLOG_NETWORK, seed, 20)
        entityManager.clear()

        log.info { "expected := $expected" }

        repeat(20) {
            val newList = staticWebpageService.findStaticWebpagesByProviderType(ServiceProviderType.PRIVATE_BLOG_NETWORK, seed, 20)
            entityManager.clear()
            Assertions.assertThat(newList)
                .extracting("webpageSeq")
                .containsExactlyElementsOf(expected.map { it.webpageSeq })
        }

    }


    companion object {
        @JvmStatic
        fun staticWebpagesProvider() = listOf(
            Arguments.of(10, 5L, 5, 5),
            Arguments.of(10, 5L, 15, 15),
            Arguments.of(10, 10L, 15, 15),
            Arguments.of(54, 10L, 60, 60)
        )
    }
}