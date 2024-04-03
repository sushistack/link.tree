package com.sushistack.linktree.entity.order

enum class OrderStatus(val tier: Int) {
    READY(0), PROCESSING_1(1), PROCESSING_2(2), PROCESSING_3(3), DONE(4)
}