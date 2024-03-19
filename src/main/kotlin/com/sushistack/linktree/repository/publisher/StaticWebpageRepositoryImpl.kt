package com.sushistack.linktree.repository.publisher

import com.querydsl.jpa.impl.JPAQueryFactory
import com.sushistack.linktree.entity.publisher.QStaticWebpage.staticWebpage
import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.entity.publisher.StaticWebpage

class StaticWebpageRepositoryImpl(private val queryFactory: JPAQueryFactory): StaticWebpageRepositoryCustom {

    override fun findStaticWebpagesProviderTypeByOrderByUsedCountAscLimit(providerType: ServiceProviderType, limit: Long): List<StaticWebpage> =
        queryFactory
            .selectFrom(staticWebpage)
            .orderBy(staticWebpage.usedCount.asc())
            .where(staticWebpage.providerType.eq(providerType))
            .limit(limit)
            .fetch()

}