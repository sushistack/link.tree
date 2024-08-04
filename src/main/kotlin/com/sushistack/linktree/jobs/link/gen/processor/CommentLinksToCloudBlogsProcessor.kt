package com.sushistack.linktree.jobs.link.gen.processor

import com.sushistack.linktree.entity.content.Comment
import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.service.CommentService
import com.sushistack.linktree.service.CommentableWebpageService
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.item.ItemProcessor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@JobScope
@Component
class CommentLinksToCloudBlogsProcessor(
    private val commentService: CommentService,
    private val commentableWebpageService: CommentableWebpageService
): ItemProcessor<LinkNode, List<LinkNode>> {

    @Value("#{jobExecutionContext['jobInstanceId']}")
    private var jobInstanceId: Long = 0

    override fun process(parentNode: LinkNode): List<LinkNode> {
        val seed = jobInstanceId + parentNode.nodeSeq
        val commentableWebpages = commentableWebpageService.findBySeed(seed = seed, fixedSize = 3)

        return commentableWebpages.map { commentableWebpage ->
            val comment = commentService.createComment(
                Comment(
                    postUrl = commentableWebpage.postUrl,
                    commentableWebpage = commentableWebpage
                )
            )

            LinkNode(
                tier = parentNode.tier + 1,
                url = commentableWebpage.postUrl,
                order = parentNode.order,
                parentNodeSeq = parentNode.nodeSeq
            ).also { it.changePublication(comment) }
        }
    }
}