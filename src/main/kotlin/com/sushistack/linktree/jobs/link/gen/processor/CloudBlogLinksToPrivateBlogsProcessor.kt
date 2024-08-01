package com.sushistack.linktree.jobs.link.gen.processor

import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.jobs.link.gen.service.LinkProvider
import com.sushistack.linktree.model.ArticleSource
import com.sushistack.linktree.service.PostService
import com.sushistack.linktree.service.StaticWebpageService
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.item.ItemProcessor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@JobScope
@Component
class CloudBlogLinksToPrivateBlogsProcessor(
    private val postService: PostService,
    private val staticWebpageService: StaticWebpageService
): ItemProcessor<LinkNode, List<LinkNode>> {
    companion object {
        private const val LINK_SIZE = 20L
    }

    @Value("#{jobExecutionContext['articleSources']}")
    private lateinit var articleSources: List<ArticleSource>

    @Value("#{jobExecutionContext['linkProvider']}")
    private lateinit var linkProvider: LinkProvider

    override fun process(parentNode: LinkNode): List<LinkNode> {
        val webpages = staticWebpageService.findStaticWebpagesByProviderType(ServiceProviderType.CLOUD_BLOG_NETWORK, LINK_SIZE)

        return webpages.map { webpage ->
            val post = postService.createPost(webpage, articleSources, linkProvider)

            LinkNode(
                url = webpage.getPostUrl(post),
                tier = parentNode.tier + 1,
                order = parentNode.order,
                parentNodeSeq = parentNode.nodeSeq,
            ).also { it.changePublication(post) }
        }
    }
}