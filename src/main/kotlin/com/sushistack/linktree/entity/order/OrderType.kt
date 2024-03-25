package com.sushistack.linktree.entity.order

enum class OrderType(val linkCount: Int) {
    STANDARD(10), DELUXE(40), PREMIUM(60), UNKNOWN(0)
}