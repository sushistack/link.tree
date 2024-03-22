package com.sushistack.linktree.service

import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.repository.link.LinkNodeRepository
import org.springframework.stereotype.Service

@Service
class LinkNodeService(private val linkNodeRepository: LinkNodeRepository) {

    fun createLinkNode(linkNode: LinkNode): LinkNode =
        linkNodeRepository.save(linkNode)

    fun findByOrder(order: Order, tier: Int): List<LinkNode> =
        linkNodeRepository.findByOrderAndTier(order, tier)

}