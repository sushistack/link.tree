package com.sushistack.linktree.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@Transactional
@SpringBootTest
class StaticWebpageServiceTest {
    private val log = KotlinLogging.logger {}

    @Autowired
    lateinit var entityManager: EntityManager

    @Autowired
    lateinit var orderService: OrderService

    @Autowired
    lateinit var staticWebpageService: StaticWebpageService

    @Test
    fun findStaticWebpagesByOrder() {
        val order = orderService.getOrderBySeq(7)
        log.info { "order := $order" }
    }
}