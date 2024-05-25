package com.sushistack.linktree.service

import com.sushistack.linktree.entity.content.Post
import com.sushistack.linktree.entity.git.GitAccount
import com.sushistack.linktree.entity.git.GitRepository
import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.publisher.StaticWebpage
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
    fun createLinkNodes() {
        // Given
        val linkNode = LinkNode()
        val linkNode2 = LinkNode()
        linkNodeService.createLinkNodes(listOf(linkNode, linkNode2))
        entityManager.flush()
        entityManager.clear()

        // Then
        val linkNodes = linkNodeRepository.findAll()
        Assertions.assertThat(linkNodes).hasSize(2)
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

    @Test
    fun findAllByOrderAndTier() {
        val tier = 1
        val order = Order(customerName = "Customer Name")
        entityManager.persist(order)
        val webpage = StaticWebpage(domain = "test.com")
        entityManager.persist(webpage)
        val post = Post(filePath = "life/test.md", webpage = webpage)
        entityManager.persist(post)
        val gitAccount = GitAccount()
        entityManager.persist(gitAccount)
        val repository = GitRepository(webpage = webpage, gitAccount = gitAccount)
        entityManager.persist(repository)
        val linkNode = LinkNode(order = order, tier = tier, repository = repository)
        linkNodeService.createLinkNode(linkNode)
        entityManager.flush()
        entityManager.clear()

        // Then
        val nodes = linkNodeService.findAllByOrderAndTier(order, tier)
        Assertions.assertThat(nodes).hasSize(1)
        Assertions.assertThat(nodes[0].repository?.webpage?.domain).isEqualTo("test.com")
    }
}