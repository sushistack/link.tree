package com.sushistack.linktree.entity.order

enum class OrderStatus(val tier: Int) {
    READY(0), PROCESSING_1(1), PROCESSING_2(2), PROCESSING_3(3), PROCCESSED(4), DEPLOYED(5), VALIDATED(6);

    companion object {
        fun next(status: OrderStatus): OrderStatus {
            return when (status) {
                READY -> PROCESSING_1
                PROCESSING_1 -> PROCESSING_2
                PROCESSING_2 -> PROCESSING_3
                PROCESSING_3 -> PROCCESSED
                PROCCESSED -> DEPLOYED
                DEPLOYED -> VALIDATED
                VALIDATED -> VALIDATED
            }
        }
    }
}