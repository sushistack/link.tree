package com.sushistack.linktree.service

import com.sushistack.linktree.entity.publisher.StaticWebpage
import com.sushistack.linktree.repository.publisher.StaticWebpageRepository
import org.springframework.stereotype.Service

@Service
class StaticWebpageService(private val staticWebpageRepository: StaticWebpageRepository) {

    fun findStaticWebpages(limit: Long): List<StaticWebpage> =
        staticWebpageRepository.findStaticWebpagesByOrderByUsedCountAscLimit(limit)

}