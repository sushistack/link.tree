package com.sushistack.linktree.repository.order

import com.sushistack.linktree.entity.order.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long>