package com.sushistack.linktree.jobs.link.gen

import com.sushistack.linktree.batch.reader.QuerydslPagingItemReader
import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.link.QLinkNode.linkNode
import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.order.QOrder
import com.sushistack.linktree.jobs.link.gen.link.LinkNodeToLinkNodesProcessor
import com.sushistack.linktree.jobs.link.gen.link.LinkNodesWriter
import com.sushistack.linktree.jobs.link.gen.order.OrderReader
import com.sushistack.linktree.jobs.link.gen.order.OrderTasklet
import com.sushistack.linktree.jobs.link.gen.order.OrderToLinkNodesProcessor
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaTransactionManager

@Configuration
class LinkGenerationJobConfig {

    companion object {
        const val ORDER_PROCESSING_SIZE = 1
        const val LINK_NODE_PROCESSING_SIZE = 40
    }

    @Bean
    fun linkGenerationJob(
        jobRepository: JobRepository,
        saveOrderStep: Step,
        addPrivateBlogsToOrderStep: Step,
        addCloudBlogsToOrderStep: Step
    ): Job =
        JobBuilder("linkGenerationJob", jobRepository)
            .start(saveOrderStep)
            .next(addPrivateBlogsToOrderStep)
            .next(addCloudBlogsToOrderStep)
            .build()

    @Bean
    fun saveOrderStep(
        jobRepository: JobRepository,
        orderTasklet: OrderTasklet,
        jpaTransactionManager: JpaTransactionManager,
    ): TaskletStep =
        StepBuilder("saveOrderStep", jobRepository)
            .tasklet(orderTasklet, jpaTransactionManager)
            .build()

    @Bean
    fun addPrivateBlogsToOrderStep(
        jobRepository: JobRepository,
        jpaTransactionManager: JpaTransactionManager,
        orderReader: OrderReader,
        orderToLinkNodesProcessor: OrderToLinkNodesProcessor,
        linkNodesWriter: LinkNodesWriter
    ): Step =
        StepBuilder("addPrivateBlogsToOrderStep", jobRepository)
            .chunk<Order, List<LinkNode>>(ORDER_PROCESSING_SIZE, jpaTransactionManager)
            .reader(orderReader)
            .processor(orderToLinkNodesProcessor)
            .writer(linkNodesWriter)
            .build()

    @Bean
    fun addCloudBlogsToOrderStep(
        jobRepository: JobRepository,
        jpaTransactionManager: JpaTransactionManager,
        linkNodeReader: QuerydslPagingItemReader<LinkNode>,
        linkNodeToLinkNodesProcessor: LinkNodeToLinkNodesProcessor,
        linkNodesWriter: LinkNodesWriter
    ): Step =
        StepBuilder("addCloudBlogsToOrderStep", jobRepository)
            .chunk<LinkNode, List<LinkNode>>(LINK_NODE_PROCESSING_SIZE, jpaTransactionManager)
            .reader(linkNodeReader)
            .processor(linkNodeToLinkNodesProcessor)
            .writer(linkNodesWriter)
            .build()


    @Bean
    @JobScope
    fun linkNodeReader(
        @Value("#{jobExecutionContext['order']}") order: Order,
        entityManagerFactory: EntityManagerFactory
    ): QuerydslPagingItemReader<LinkNode> {
        val tier = order.orderStatus.tier
        return QuerydslPagingItemReader(entityManagerFactory) { queryFactory, offset, limit ->
            queryFactory
                .selectFrom(linkNode)
                .innerJoin(QOrder.order)
                .on(
                    linkNode.order.orderSeq.eq(QOrder.order.orderSeq)
                        .and(QOrder.order.orderSeq.eq(order.orderSeq))
                )
                .where(linkNode.tier.eq(tier))
                .offset(offset.toLong())
                .limit(limit.toLong())
                .fetch()
        }
    }

}