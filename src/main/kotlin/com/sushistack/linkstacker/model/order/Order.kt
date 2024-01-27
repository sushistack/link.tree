package com.sushistack.linkstacker.model.order

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("order")
class Order (
    @Id val id: String? = null,
    val orderType: String,
    val url: String,
    val orderStatus: OrderStatus
)