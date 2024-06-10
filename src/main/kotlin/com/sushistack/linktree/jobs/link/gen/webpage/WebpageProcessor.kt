package com.sushistack.linktree.jobs.link.gen.webpage

import com.sushistack.linktree.entity.content.Post
import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.service.PostService
import com.sushistack.linktree.service.StaticWebpageService
import org.springframework.batch.item.ItemProcessor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class WebpageProcessor(
    private val postService: PostService,
    private val staticWebpageService: StaticWebpageService,
    @Value("#{jobExecutionContext['keywords']}") private val keywords: List<String>
): ItemProcessor<LinkNode, List<LinkNode>> {

    override fun process(parentNode: LinkNode): List<LinkNode> {
        val webpages = staticWebpageService.findStaticWebpagesByProviderType(ServiceProviderType.CLOUD_BLOG_NETWORK, 20)

        return webpages.map { webpage ->
            val post = Post(filePath = "life/test.md", webpage = webpage)
            postService.createPost(post, keywords)
            LinkNode(
                order = parentNode.order,
                url = webpage.getPostUrl(post),
                repository = webpage.repository,
                parentNodeSeq = parentNode.nodeSeq,
                tier = parentNode.tier + 1
            )
        }
    }
}