package com.sushistack.linkstacker.model.order

import jakarta.persistence.*

@Entity
@Table(name = "ls_order")
class Order (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id", nullable = false)
    val orderId: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    val orderType: OrderType = OrderType.UNKNOWN,

    @Column(name = "url", nullable = false)
    val url: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    val orderStatus: OrderStatus = OrderStatus.READY
) {
    companion object {
        fun empty() = Order()
    }
}