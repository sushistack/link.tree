package com.sushistack.linktree.jobs.link.gen

import com.sushistack.linktree.batch.reader.QuerydslPagingItemReader
import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.jobs.link.gen.listener.CloudBlogsStepListener
import com.sushistack.linktree.jobs.link.gen.listener.CommentStepListener
import com.sushistack.linktree.jobs.link.gen.listener.PrivateBlogsStepListener
import com.sushistack.linktree.jobs.link.gen.processor.CloudBlogLinksToPrivateBlogsProcessor
import com.sushistack.linktree.jobs.link.gen.processor.CommentLinksToCloudBlogsProcessor
import com.sushistack.linktree.jobs.link.gen.processor.PrivateBlogLinksToOrderProcessor
import com.sushistack.linktree.jobs.link.gen.reader.OrderReader
import com.sushistack.linktree.jobs.link.gen.tasklet.ClearingInitializationTask
import com.sushistack.linktree.jobs.link.gen.tasklet.InitializationTasklet
import com.sushistack.linktree.jobs.link.gen.tasklet.OrderTasklet
import com.sushistack.linktree.jobs.link.gen.tasklet.ReportTasklet
import com.sushistack.linktree.jobs.link.gen.writer.LinkNodeWriter
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaTransactionManager

@Configuration
@ConditionalOnProperty(value = ["spring.batch.job.name"], havingValue = "linkGenerationJob")
class LinkGenerationJobConfig {

    companion object {
        const val ORDER_CHUNK_SIZE = 1
        const val WEBPAGE_CHUNK_SIZE = 40
        const val COMMENT_CHUNK_SIZE = 200
    }

    @Bean
    fun linkGenerationJob(
        jobRepository: JobRepository,
        initializationStep: Step,
        clearingInitializationStep: Step,
        saveOrderStep: Step,
        addPrivateBlogsToOrderStep: Step,
        addCloudBlogsToOrderStep: Step,
        addCommentsToLinkNodesStep: Step,
        saveToExcelStep: Step
    ): Job =
        JobBuilder("linkGenerationJob", jobRepository)
            .start(initializationStep)
            .next(saveOrderStep)
            .next(addPrivateBlogsToOrderStep)
            .next(addCloudBlogsToOrderStep)
            .next(addCommentsToLinkNodesStep)
            .next(saveToExcelStep)
            .build()

    @Bean
    fun initializationStep(
        jobRepository: JobRepository,
        initializationTasklet: InitializationTasklet,
        jpaTransactionManager: JpaTransactionManager
    ): TaskletStep =
        StepBuilder("initializationStep", jobRepository)
            .tasklet(initializationTasklet, jpaTransactionManager)
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
        privateBlogLinksToOrderProcessor: PrivateBlogLinksToOrderProcessor,
        linkNodeWriter: LinkNodeWriter,
        privateBlogsStepListener: PrivateBlogsStepListener
    ): Step =
        StepBuilder("addPrivateBlogsToOrderStep", jobRepository)
            .chunk<Order, List<LinkNode>>(ORDER_CHUNK_SIZE, jpaTransactionManager)
            .reader(orderReader)
            .processor(privateBlogLinksToOrderProcessor)
            .writer(linkNodeWriter)
            .listener(privateBlogsStepListener)
            .build()


    @Bean
    fun addCloudBlogsToOrderStep(
        jobRepository: JobRepository,
        jpaTransactionManager: JpaTransactionManager,
        linkNodeReader: QuerydslPagingItemReader<LinkNode>,
        cloudBlogLinksToPrivateBlogsProcessor: CloudBlogLinksToPrivateBlogsProcessor,
        linkNodeWriter: LinkNodeWriter,
        cloudBlogsStepListener: CloudBlogsStepListener
    ): Step =
        StepBuilder("addCloudBlogsToOrderStep", jobRepository)
            .chunk<LinkNode, List<LinkNode>>(WEBPAGE_CHUNK_SIZE, jpaTransactionManager)
            .reader(linkNodeReader)
            .processor(cloudBlogLinksToPrivateBlogsProcessor)
            .writer(linkNodeWriter)
            .listener(cloudBlogsStepListener)
            .build()


    @Bean
    fun addCommentsToLinkNodesStep(
        jobRepository: JobRepository,
        jpaTransactionManager: JpaTransactionManager,
        linkNodeReader: QuerydslPagingItemReader<LinkNode>,
        commentLinksToCloudBlogsProcessor: CommentLinksToCloudBlogsProcessor,
        linkNodeWriter: LinkNodeWriter,
        commentStepListener: CommentStepListener
    ): Step =
        StepBuilder("addCommentsToLinkNodesStep", jobRepository)
            .chunk<LinkNode, List<LinkNode>>(COMMENT_CHUNK_SIZE, jpaTransactionManager)
            .reader(linkNodeReader)
            .processor(commentLinksToCloudBlogsProcessor)
            .writer(linkNodeWriter)
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


    @Bean
    fun clearingInitializationStep(
        jobRepository: JobRepository,
        clearingInitializationTask: ClearingInitializationTask,
        jpaTransactionManager: JpaTransactionManager
    ): TaskletStep =
        StepBuilder("clearingInitializationStep", jobRepository)
            .tasklet(clearingInitializationTask, jpaTransactionManager)
            .build()

}