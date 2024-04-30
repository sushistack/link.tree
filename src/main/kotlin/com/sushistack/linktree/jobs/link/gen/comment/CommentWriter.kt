package com.sushistack.linktree.jobs.link.gen.comment

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

@Component
class CommentWriter(private val linkNodeService: LinkNodeService): ItemWriter<List<LinkNode>> {
    val log = KotlinLogging.logger {}

    override fun write(chunk: Chunk<out List<LinkNode>>) {
        val nodes = chunk.flatMap { it }
        linkNodeService.createLinkNodes(nodes)
        log.info { "Successfully created link nodes. (size: ${nodes.size})" }
    }
}