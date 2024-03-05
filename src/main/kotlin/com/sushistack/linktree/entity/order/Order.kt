package com.sushistack.linktree.entity.order

import com.sushistack.linktree.entity.BaseTimeEntity
import com.sushistack.linktree.entity.link.LinkNode
import jakarta.persistence.*

@Entity
@Table(name = "lt_order")
class Order (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_seq", nullable = false)
    val orderSeq: Long = 0,

    @Enumerated(EnumType.STRING)
    @Column(name = "order_type", nullable = false)
    val orderType: OrderType = OrderType.UNKNOWN,

    @Column(name = "target_url", nullable = false)
    val targetUrl: String = "",

    @Column(name = "customer_name", nullable = false)
    val customerName: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    val orderStatus: OrderStatus = OrderStatus.READY,

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    val linkNodes: List<LinkNode> = emptyList(),
): BaseTimeEntity()