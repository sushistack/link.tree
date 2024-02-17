package com.sushistack.linkstacker.service

import com.sushistack.linkstacker.entity.order.Order
import com.sushistack.linkstacker.entity.order.OrderStatus
import com.sushistack.linkstacker.entity.order.OrderType
import com.sushistack.linkstacker.log
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import org.springframework.test.annotation.Rollback
import kotlin.test.Test

@Profile("test")
@SpringBootTest
@Rollback(value = true)
class OrderServiceTest {

    @Autowired
    private lateinit var orderService: OrderService

    @BeforeEach
    fun setUp() {
    }

    @Test
    @DisplayName("basic CRUD Test.")
    fun basicCRUDTest() {
        val order = Order(
            orderType = OrderType.STANDARD,
            targetUrl = "https://test.com",
            customerName = "who is it",
            orderStatus = OrderStatus.READY
        )

        val saved = orderService.createOrder(order)
        log.info { "saved := $saved" }
    }
}