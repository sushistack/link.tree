package com.sushistack.linktree.repository.order

import com.sushistack.linktree.entity.order.Order
import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Transactional
class OrderGitRepositoryUtilTest {

    @Autowired
    lateinit var entityManager: EntityManager
    @Autowired
    lateinit var orderRepository: OrderRepository

    @Test
    fun saveTest() {
        // Given
        Order().let { entityManager.persist(it) }
        entityManager.flush()
        entityManager.clear()

        // Then
        val orders = orderRepository.findAll()
        Assertions.assertThat(orders.size).isEqualTo(1)
    }
}