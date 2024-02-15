package com.sushistack.linkstacker.service

import com.sushistack.linkstacker.entity.order.Order
import com.sushistack.linkstacker.repository.order.OrderRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

val log = KotlinLogging.logger {}

@Service
class OrderService(private val orderRepository: OrderRepository) {

    fun createOrder(order: Order): Order =
        orderRepository.save(order)

    fun getOrderBySeq(orderSeq: Long): Order =
        orderRepository.findById(orderSeq).orElseThrow()

}