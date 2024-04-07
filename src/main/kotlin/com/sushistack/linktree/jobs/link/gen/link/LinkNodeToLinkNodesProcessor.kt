package com.sushistack.linktree.jobs.link.gen.link

import com.sushistack.linktree.entity.content.Post
import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.service.PostService
import com.sushistack.linktree.service.StaticWebpageService
import org.springframework.batch.item.ItemProcessor
import org.springframework.stereotype.Component

@Component
class LinkNodeToLinkNodesProcessor(
    private val postService: PostService,
    private val staticWebpageService: StaticWebpageService
): ItemProcessor<LinkNode, List<LinkNode>> {

    override fun process(parentNode: LinkNode): List<LinkNode> {
        val webpages = staticWebpageService.findStaticWebpagesByProviderType(ServiceProviderType.CLOUD_BLOG_NETWORK, 20)

        return webpages.map { webpage ->
            val post = Post(webpage = webpage)
            postService.createPost(post)
            LinkNode(order = parentNode.order, repository = webpage.repository, parentNodeSeq = parentNode.nodeSeq, tier = parentNode.tier + 1)
        }
    }
}