package com.sushistack.linktree.repository.publisher

import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.entity.publisher.StaticWebpage

interface StaticWebpageRepositoryCustom {
    fun findAllByProviderType(providerType: ServiceProviderType): List<StaticWebpage>
    fun findAllByOrderAndProviderType(order: Order, providerType: ServiceProviderType): List<StaticWebpage>
}