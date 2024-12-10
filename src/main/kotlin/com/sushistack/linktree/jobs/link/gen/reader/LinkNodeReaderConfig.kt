package com.sushistack.linktree.jobs.link.gen.reader

import com.sushistack.linktree.batch.reader.QuerydslPagingItemReader
import com.sushistack.linktree.entity.content.QPublication.publication
import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.link.QLinkNode.linkNode
import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.order.QOrder
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LinkNodeReaderConfig {

    @Bean
    @JobScope
    fun linkNodeReader(
        @Value("#{jobExecutionContext['order']}") order: Order,
        entityManagerFactory: EntityManagerFactory
    ): QuerydslPagingItemReader<LinkNode> =
        QuerydslPagingItemReader(entityManagerFactory) { queryFactory, offset, limit ->
            queryFactory
                .selectFrom(linkNode)
                .join(linkNode.order, QOrder.order).fetchJoin()
                .join(linkNode.publication, publication).fetchJoin()
                .where(
                    linkNode.tier.eq(order.orderStatus.phase - 1)
                        .and(linkNode.order.orderSeq.eq(QOrder.order.orderSeq))
                        .and(QOrder.order.orderSeq.eq(order.orderSeq))
                )
                .offset(offset.toLong())
                .limit(limit.toLong())
                .fetch()
        }

}