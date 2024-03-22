package com.sushistack.linktree.repository.link

import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.order.Order

interface LinkNodeRepositoryCustom {
    fun findByOrderAndTier(order: Order, tier: Int): List<LinkNode>
}