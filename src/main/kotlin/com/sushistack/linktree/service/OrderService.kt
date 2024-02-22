package com.sushistack.linktree.service

import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.repository.order.OrderRepository
import org.springframework.stereotype.Service

@Service
class OrderService(private val orderRepository: OrderRepository) {

    fun createOrder(order: Order): Order =
        orderRepository.save(order)

    fun getOrderBySeq(orderSeq: Long): Order =
        orderRepository.findById(orderSeq).orElseThrow()

}