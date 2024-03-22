package com.sushistack.linktree.service

import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.repository.link.LinkNodeRepository
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
class LinkNodeServiceTest {

    @Autowired
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var linkNodeRepository: LinkNodeRepository

    @Autowired
    lateinit var linkNodeService: LinkNodeService

    @Test
    fun createLinkNode() {
        // Given
        val linkNode = LinkNode()
        linkNodeService.createLinkNode(linkNode)
        entityManager.flush()
        entityManager.clear()

        // Then
        val linkNodes = linkNodeRepository.findAll()
        Assertions.assertThat(linkNodes).hasSize(1)
    }

    @Test
    fun findByOrder() {
        // Given
        val tier = 1
        val order = Order(customerName = "Customer Name")
        entityManager.persist(order)
        val linkNode = LinkNode(order = order, tier = tier)
        linkNodeService.createLinkNode(linkNode)
        entityManager.flush()
        entityManager.clear()

        // Then
        val nodes = linkNodeService.findByOrder(order, tier)
        Assertions.assertThat(nodes).hasSize(1)
        Assertions.assertThat(nodes[0].order?.orderSeq).isEqualTo(order.orderSeq)
    }
}