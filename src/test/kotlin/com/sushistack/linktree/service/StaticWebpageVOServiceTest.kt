package com.sushistack.linktree.service

import com.sushistack.linktree.entity.git.GitAccount
import com.sushistack.linktree.entity.git.GitRepository
import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.entity.publisher.StaticWebpage
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
class StaticWebpageVOServiceTest {
    @Autowired
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var staticWebpageService: StaticWebpageService

    @DisplayName("find static webpage with limit size")
    @ParameterizedTest(name = "[{index}] webpage size = {0}, limit = {1}, expected size = {2}")
    @MethodSource("staticWebpagesProvider")
    fun findStaticWebpages(webpageSize: Long, limit: Long, expectedSize: Int) {
        // Given
        val account = GitAccount()
        entityManager.persist(account)

        val staticWebpages = (1..webpageSize).map { StaticWebpage(domain = "", providerType = ServiceProviderType.UNKNOWN, usedCount = 0) }
        staticWebpages.forEach { web -> entityManager.persist(web) }
        staticWebpages.map { web -> GitRepository(webpage = web, gitAccount = account) }.forEach { entityManager.persist(it) }
        entityManager.flush()
        entityManager.clear()

        val webpages = staticWebpageService.findStaticWebpagesByProviderType(ServiceProviderType.UNKNOWN, limit)
        Assertions.assertThat(webpages).hasSize(expectedSize)
    }


    companion object {
        @JvmStatic
        fun staticWebpagesProvider() = listOf(
            Arguments.of(10, 5, 5),
            Arguments.of(10, 15, 10)
        )
    }
}