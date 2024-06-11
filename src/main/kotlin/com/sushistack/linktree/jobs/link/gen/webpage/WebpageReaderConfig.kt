package com.sushistack.linktree.jobs.link.gen.webpage

import com.sushistack.linktree.batch.reader.QuerydslPagingItemReader
import com.sushistack.linktree.entity.content.QPublication
import com.sushistack.linktree.entity.content.QPublication.*
import com.sushistack.linktree.entity.git.QGitAccount
import com.sushistack.linktree.entity.git.QGitRepository
import com.sushistack.linktree.entity.git.QGitRepository.gitRepository
import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.link.QLinkNode.linkNode
import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.order.QOrder
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebpageReaderConfig {
    private val log = KotlinLogging.logger {}

    @Bean
    @JobScope
    fun webpageReader(
        @Value("#{jobExecutionContext['order']}") order: Order,
        entityManagerFactory: EntityManagerFactory
    ): QuerydslPagingItemReader<LinkNode> {
        val prevTier = order.orderStatus.tier - 1
        return QuerydslPagingItemReader(entityManagerFactory) { queryFactory, offset, limit ->
            queryFactory
                .selectFrom(linkNode)
                .join(linkNode.order, QOrder.order).fetchJoin()
                .join(linkNode.publication, publication).fetchJoin()
                .where(
                    linkNode.tier.eq(prevTier)
                        .and(linkNode.order.orderSeq.eq(QOrder.order.orderSeq))
                        .and(QOrder.order.orderSeq.eq(order.orderSeq))
                )
                .offset(offset.toLong())
                .limit(limit.toLong())
                .fetch()
        }
    }

}