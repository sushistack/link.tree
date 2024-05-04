package com.sushistack.linktree.repository.publisher

import com.querydsl.jpa.impl.JPAQueryFactory
import com.sushistack.linktree.entity.git.QGitAccount.gitAccount
import com.sushistack.linktree.entity.git.QGitRepository.gitRepository
import com.sushistack.linktree.entity.publisher.QStaticWebpage.staticWebpage
import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.entity.publisher.StaticWebpage

class StaticWebpageRepositoryImpl(private val queryFactory: JPAQueryFactory): StaticWebpageRepositoryCustom {

    override fun findStaticWebpagesProviderTypeByOrderByUsedCountAscLimit(providerType: ServiceProviderType, limit: Long): List<StaticWebpage> =
        queryFactory
            .selectFrom(staticWebpage)
            .join(staticWebpage.repository, gitRepository).fetchJoin()
            .join(gitRepository.gitAccount, gitAccount).fetchJoin()
            .where(staticWebpage.providerType.eq(providerType))
            .orderBy(staticWebpage.usedCount.asc())
            .limit(limit)
            .fetch()

}