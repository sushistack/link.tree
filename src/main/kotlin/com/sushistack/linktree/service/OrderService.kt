package com.sushistack.linktree.service

import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.order.OrderStatus
import com.sushistack.linktree.repository.order.OrderRepository
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class OrderService(private val orderRepository: OrderRepository) {

    fun createOrder(order: Order): Order =
        orderRepository.save(order)

    fun updateOrder(order: Order): Order =
        orderRepository.save(order)

    fun getOrderBySeq(orderSeq: Long): Order =
        orderRepository.findById(orderSeq).orElseThrow()

    fun findTop1ByOrderStatusOrderByOrderSeqDesc(orderStatus: OrderStatus): Optional<Order> =
        orderRepository.findTop1ByOrderStatusOrderByOrderSeqDesc(orderStatus)

    fun findByOrderStatus(orderStatus: OrderStatus): List<Order> =
        orderRepository.findByOrderStatus(orderStatus)
}