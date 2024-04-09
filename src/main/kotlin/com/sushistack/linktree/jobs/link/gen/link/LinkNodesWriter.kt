package com.sushistack.linktree.jobs.link.gen.link

import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.order.OrderStatus
import com.sushistack.linktree.service.LinkNodeService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@JobScope
@Component
class LinkNodesWriter(private val linkNodeService: LinkNodeService): ItemWriter<List<LinkNode>> {
    val log = KotlinLogging.logger {}

    @Value("#{jobExecutionContext['order']}")
    lateinit var order: Order

    override fun write(chunk: Chunk<out List<LinkNode>>) {
        chunk.forEach { linkNodes -> linkNodeService.createLinkNodes(linkNodes) }
        log.info { "Successfully created link nodes." }
        order.orderStatus = OrderStatus.next(order.orderStatus)
        log.info { "Order Status := [${order.orderStatus}]" }
    }
}