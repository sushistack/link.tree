package com.sushistack.linktree.repository.order

import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.order.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OrderRepository : JpaRepository<Order, Long> {
    fun findTop1ByOrderStatusOrderByOrderSeqDesc(orderStatus: OrderStatus): Optional<Order>
    fun findByOrderStatus(orderStatus: OrderStatus): List<Order>
}