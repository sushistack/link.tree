package com.sushistack.linktree.service

import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.entity.publisher.StaticWebpage
import com.sushistack.linktree.repository.publisher.StaticWebpageRepository
import org.springframework.stereotype.Service

@Service
class StaticWebpageService(private val staticWebpageRepository: StaticWebpageRepository) {

    fun findStaticWebpagesByProviderType(providerType: ServiceProviderType, limit: Long): List<StaticWebpage> =
        staticWebpageRepository.findStaticWebpagesProviderTypeByOrderByUsedCountAscLimit(providerType = providerType, limit = limit)

}