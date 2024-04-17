package com.sushistack.linktree.jobs.link.gen.link

import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.service.CommentService
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component

@Component
class CommentProcessor(
    private val commentService: CommentService
): ItemProcessor<LinkNode, List<LinkNode>> {
    override fun process(parentNode: LinkNode): List<LinkNode> {
        val comments = commentService.findByOrderByUsedCountLimit(limit = 3)

        return comments.map { comment ->
            LinkNode(order = parentNode.order, tier = parentNode.tier + 1, url = comment.postUrl)
        }
    }
}