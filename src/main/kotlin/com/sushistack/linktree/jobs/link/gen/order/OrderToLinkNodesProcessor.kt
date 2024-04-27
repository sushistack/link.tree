package com.sushistack.linktree.jobs.link.gen.order

import com.sushistack.linktree.entity.content.Post
import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.service.PostService
import com.sushistack.linktree.service.StaticWebpageService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component

@Component
class OrderToLinkNodesProcessor(
    private val postService: PostService,
    private val staticWebpageService: StaticWebpageService
): ItemProcessor<Order, List<LinkNode>> {
    private val log = KotlinLogging.logger {}

    override fun process(order: Order): List<LinkNode> {
        val webpages = staticWebpageService.findStaticWebpagesByProviderType(ServiceProviderType.PRIVATE_BLOG_NETWORK, order.orderType.linkCount.toLong())

        return webpages.map { webpage ->
            val post = Post(webpage = webpage)
            postService.createPost(post)
            LinkNode(order = order, repository = webpage.repository, tier = order.orderStatus.tier + 1, parentNodeSeq = order.orderSeq)
        }
    }
}