package com.sushistack.linktree.jobs.link.gen.link

import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.service.LinkNodeService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.ItemWriter
import org.springframework.stereotype.Component

@Component
class LinkNodesWriter(private val linkNodeService: LinkNodeService): ItemWriter<List<LinkNode>> {
    val log = KotlinLogging.logger {}

    override fun write(chunk: Chunk<out List<LinkNode>>) {
        chunk.forEach { linkNodes -> linkNodeService.createLinkNodes(linkNodes) }
        log.info { "Successfully created link nodes" }
    }
}