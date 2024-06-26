package com.sushistack.linktree.repository.link

import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.model.dto.LinkNodeRepositoryDTO

interface LinkNodeRepositoryCustom {
    fun findByOrderAndTier(order: Order, tier: Int): List<LinkNode>
    fun findAllByOrderAndTier(order: Order, tier: Int): List<LinkNode>
    fun findWithPostByOrder(order: Order, tier: Int): List<LinkNodeRepositoryDTO>
}