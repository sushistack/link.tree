package com.sushistack.linktree.repository.publisher

import com.sushistack.linktree.entity.git.GitAccount
import com.sushistack.linktree.entity.git.GitRepository
import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.entity.publisher.StaticWebpage
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Transactional
class StaticWebpageVOSimpleGitRepositoryVOUtilTest {
    @Autowired
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var staticWebpageRepository: StaticWebpageRepository

    @Test
    fun findStaticWebpagesByOrderByUsedCountAscLimitTest() {
        // Given
        val account = GitAccount()
        entityManager.persist(account)
        val staticWebpages = listOf(
            StaticWebpage(domain = "", providerType = ServiceProviderType.UNKNOWN, usedCount = 0),
            StaticWebpage(domain = "", providerType = ServiceProviderType.UNKNOWN, usedCount = 0),
            StaticWebpage(domain = "", providerType = ServiceProviderType.UNKNOWN, usedCount = 0),
            StaticWebpage(domain = "", providerType = ServiceProviderType.UNKNOWN, usedCount = 0),
            StaticWebpage(domain = "", providerType = ServiceProviderType.UNKNOWN, usedCount = 0),
            StaticWebpage(domain = "", providerType = ServiceProviderType.UNKNOWN, usedCount = 0),
            StaticWebpage(domain = "", providerType = ServiceProviderType.UNKNOWN, usedCount = 0),
            StaticWebpage(domain = "", providerType = ServiceProviderType.UNKNOWN, usedCount = 0),
            StaticWebpage(domain = "", providerType = ServiceProviderType.UNKNOWN, usedCount = 0),
            StaticWebpage(domain = "", providerType = ServiceProviderType.UNKNOWN, usedCount = 0),
            StaticWebpage(domain = "", providerType = ServiceProviderType.UNKNOWN, usedCount = 0),
            StaticWebpage(domain = "", providerType = ServiceProviderType.UNKNOWN, usedCount = 0)
        )
        staticWebpages.forEach { web -> entityManager.persist(web) }
        staticWebpages.map { web -> GitRepository(webpage = web, gitAccount = account) }.forEach { entityManager.persist(it) }
        entityManager.flush()
        entityManager.clear()

        // Then
        val webpages = staticWebpageRepository.findStaticWebpagesProviderTypeByOrderByUsedCountAscLimit(providerType = ServiceProviderType.UNKNOWN, limit = 10)
        Assertions.assertThat(webpages.size).isEqualTo(10)
    }
}