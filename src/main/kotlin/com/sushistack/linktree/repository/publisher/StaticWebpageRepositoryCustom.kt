package com.sushistack.linktree.repository.publisher

import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.entity.publisher.StaticWebpage

interface StaticWebpageRepositoryCustom {
    fun findStaticWebpagesProviderTypeByOrderByUsedCountAscLimit(providerType: ServiceProviderType, limit: Long): List<StaticWebpage>
}