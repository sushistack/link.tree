package com.sushistack.linktree.jobs.link.gen.order

import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.order.OrderStatus
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.item.ItemReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@JobScope
@Component
class OrderReader: ItemReader<Order> {
    private val log = KotlinLogging.logger {}
    private var processed = false

    @Value("#{jobExecutionContext['order']}")
    lateinit var order: Order

    override fun read(): Order? {
        return if (!processed) {
            processed = true
            order.orderStatus = OrderStatus.STAGED
            log.info { "Read $order" }
            order
        } else null
    }
}