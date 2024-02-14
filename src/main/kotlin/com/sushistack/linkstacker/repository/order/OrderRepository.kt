package com.sushistack.linkstacker.repository.order

import com.sushistack.linkstacker.entity.order.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long>