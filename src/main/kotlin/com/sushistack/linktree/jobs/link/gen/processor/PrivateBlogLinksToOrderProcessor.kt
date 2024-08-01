package com.sushistack.linktree.jobs.link.gen.processor

import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.jobs.link.gen.service.LinkProvider
import com.sushistack.linktree.model.ArticleSource
import com.sushistack.linktree.service.PostService
import com.sushistack.linktree.service.StaticWebpageService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.item.ItemProcessor
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@JobScope
@Component
class PrivateBlogLinksToOrderProcessor(
    private val postService: PostService,
    private val staticWebpageService: StaticWebpageService
): ItemProcessor<Order, List<LinkNode>> {

    private val log = KotlinLogging.logger {}

    @Value("#{jobExecutionContext['articleSources']}")
    private lateinit var articleSources: List<ArticleSource>

    @Value("#{jobExecutionContext['linkProvider']}")
    private lateinit var linkProvider: LinkProvider

    override fun process(order: Order): List<LinkNode> {
        log.info { "articleSources.size = ${articleSources.size}" }
        val webpages = staticWebpageService.findStaticWebpagesByProviderType(ServiceProviderType.PRIVATE_BLOG_NETWORK, order.orderType.linkCount.toLong())

        return webpages.map { webpage ->
            val post = postService.createPost(webpage, articleSources, linkProvider)

            LinkNode(
                tier = order.orderStatus.tier,
                url = webpage.getPostUrl(post),
                order = order,
                parentNodeSeq = order.orderSeq
            ).also { it.changePublication(post) }
        }
    }
}