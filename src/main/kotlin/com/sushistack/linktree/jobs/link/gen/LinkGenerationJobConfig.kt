package com.sushistack.linktree.jobs.link.gen

import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.jobs.link.gen.link.LinkNodesWriter
import com.sushistack.linktree.jobs.link.gen.order.OrderReader
import com.sushistack.linktree.jobs.link.gen.order.OrderTasklet
import com.sushistack.linktree.jobs.link.gen.order.OrderToLinkNodesProcessor
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaTransactionManager

@Configuration
class LinkGenerationJobConfig {

    companion object {
        const val ORDER_PROCESSING_SIZE = 1
    }

    @Bean
    fun linkGenerationJob(
        jobRepository: JobRepository,
        saveOrderStep: Step,
        addPrivateBlogsToOrderStep: Step,
    ): Job =
        JobBuilder("linkGenerationJob", jobRepository)
            .start(saveOrderStep)
            .next(addPrivateBlogsToOrderStep)
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

}