package com.sushistack.linktree.jobs.link.gen

import com.sushistack.linktree.batch.reader.QuerydslPagingItemReader
import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.jobs.link.gen.comment.CommentProcessor
import com.sushistack.linktree.jobs.link.gen.comment.CommentWriter
import com.sushistack.linktree.jobs.link.gen.listener.CloudBlogsStepListener
import com.sushistack.linktree.jobs.link.gen.listener.CommentStepListener
import com.sushistack.linktree.jobs.link.gen.listener.PrivateBlogsStepListener
import com.sushistack.linktree.jobs.link.gen.order.OrderReader
import com.sushistack.linktree.jobs.link.gen.order.OrderTasklet
import com.sushistack.linktree.jobs.link.gen.order.OrderToLinkNodesProcessor
import com.sushistack.linktree.jobs.link.gen.report.ReportTasklet
import com.sushistack.linktree.jobs.link.gen.webpage.WebpageProcessor
import com.sushistack.linktree.jobs.link.gen.webpage.WebpageWriter
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
        const val ORDER_CHUNK_SIZE = 1
        const val WEBPAGE_CHUNK_SIZE = 40
        const val COMMENT_CHUNK_SIZE = 200
    }

    @Bean
    fun linkGenerationJob(
        jobRepository: JobRepository,
        saveOrderStep: Step,
        addPrivateBlogsToOrderStep: Step,
        addCloudBlogsToOrderStep: Step,
        addCommentsToLinkNodesStep: Step,
        saveToExcelStep: Step
    ): Job =
        JobBuilder("linkGenerationJob", jobRepository)
            .start(saveOrderStep)
            .next(addPrivateBlogsToOrderStep)
            .next(addCloudBlogsToOrderStep)
            .next(addCommentsToLinkNodesStep)
            .next(saveToExcelStep)
            .build()


    @Bean
    fun saveOrderStep(
        jobRepository: JobRepository,
        orderTasklet: OrderTasklet,
        jpaTransactionManager: JpaTransactionManager
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
        webpageWriter: WebpageWriter,
        privateBlogsStepListener: PrivateBlogsStepListener
    ): Step =
        StepBuilder("addPrivateBlogsToOrderStep", jobRepository)
            .chunk<Order, List<LinkNode>>(ORDER_CHUNK_SIZE, jpaTransactionManager)
            .reader(orderReader)
            .processor(orderToLinkNodesProcessor)
            .writer(webpageWriter)
            .listener(privateBlogsStepListener)
            .build()


    @Bean
    fun addCloudBlogsToOrderStep(
        jobRepository: JobRepository,
        jpaTransactionManager: JpaTransactionManager,
        webpageReader: QuerydslPagingItemReader<LinkNode>,
        webpageProcessor: WebpageProcessor,
        webpageWriter: WebpageWriter,
        cloudBlogsStepListener: CloudBlogsStepListener
    ): Step =
        StepBuilder("addCloudBlogsToOrderStep", jobRepository)
            .chunk<LinkNode, List<LinkNode>>(WEBPAGE_CHUNK_SIZE, jpaTransactionManager)
            .reader(webpageReader)
            .processor(webpageProcessor)
            .writer(webpageWriter)
            .listener(cloudBlogsStepListener)
            .build()


    @Bean
    fun addCommentsToLinkNodesStep(
        jobRepository: JobRepository,
        jpaTransactionManager: JpaTransactionManager,
        commentReader: QuerydslPagingItemReader<LinkNode>,
        commentProcessor: CommentProcessor,
        commentWriter: CommentWriter,
        commentStepListener: CommentStepListener
    ): Step =
        StepBuilder("addCommentsToLinkNodesStep", jobRepository)
            .chunk<LinkNode, List<LinkNode>>(COMMENT_CHUNK_SIZE, jpaTransactionManager)
            .reader(commentReader)
            .processor(commentProcessor)
            .writer(commentWriter)
            .listener(commentStepListener)
            .build()


    @Bean
    fun saveToExcelStep(
        jobRepository: JobRepository,
        jpaTransactionManager: JpaTransactionManager,
        reportTasklet: ReportTasklet
    ): Step =
        StepBuilder("saveToExcelStep", jobRepository)
            .tasklet(reportTasklet, jpaTransactionManager)
            .build()
}