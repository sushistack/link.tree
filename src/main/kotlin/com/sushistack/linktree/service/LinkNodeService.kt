package com.sushistack.linktree.service

import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.model.dto.LinkNodeRepositoryDTO
import com.sushistack.linktree.repository.link.LinkNodeRepository
import org.springframework.stereotype.Service

@Service
class LinkNodeService(private val linkNodeRepository: LinkNodeRepository) {

    fun createLinkNode(linkNode: LinkNode): LinkNode =
        linkNodeRepository.save(linkNode)

    fun createLinkNodes(linkNodes: List<LinkNode>): List<LinkNode> =
        linkNodeRepository.saveAll(linkNodes)

    fun findByOrder(order: Order, tier: Int): List<LinkNode> =
        linkNodeRepository.findByOrderAndTier(order, tier)

    fun findAllByOrderAndTier(order: Order, tier: Int): List<LinkNode> =
        linkNodeRepository.findAllByOrderAndTier(order, tier)

    fun findWithPostByOrder(order: Order, tier: Int): List<LinkNodeRepositoryDTO> =
        linkNodeRepository.findWithPostByOrder(order, tier)

}