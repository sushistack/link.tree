package com.sushistack.linktree.repository.publisher

import com.sushistack.linktree.entity.publisher.StaticWebpage

interface StaticWebpageRepositoryCustom {
    fun findStaticWebpagesByOrderByUsedCountAscLimit(limit: Long): List<StaticWebpage>
}