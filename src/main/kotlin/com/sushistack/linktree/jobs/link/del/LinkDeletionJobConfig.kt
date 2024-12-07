package com.sushistack.linktree.jobs.link.del

import com.sushistack.linktree.jobs.link.gen.listener.JobCompletionNotificationListener
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaTransactionManager

@Configuration
@ConditionalOnProperty(value = ["spring.batch.job.name"], havingValue = "LinkDeletionJob")
class LinkDeletionJobConfig {

    @Bean
    fun linkDeletionJob(
        jobRepository: JobRepository,
        fixOrderStep: Step,
        deleteLinksStep: Step,
        syncOriginStep: Step,
        jobListener: JobCompletionNotificationListener
    ): Job =
        JobBuilder("linkDeletionJob", jobRepository)
            .start(fixOrderStep)
            .next(deleteLinksStep)
            .next(syncOriginStep)
            .listener(jobListener)
            .build()

    @Bean
    fun fixOrderStep(
        fixOrderTasklet: Tasklet,
        jobRepository: JobRepository,
        jpaTransactionManager: JpaTransactionManager
    ): Step =
        StepBuilder("fixOrderStep", jobRepository)
            .tasklet(fixOrderTasklet, jpaTransactionManager)
            .build()

    @Bean
    fun deleteLinksStep(
        deleteLinkTasklet: Tasklet,
        jobRepository: JobRepository,
        jpaTransactionManager: JpaTransactionManager
    ): Step =
        StepBuilder("deleteLinksStep", jobRepository)
            .tasklet(deleteLinkTasklet, jpaTransactionManager)
            .build()

    @Bean
    fun syncOriginStep(
        syncOriginTasklet: Tasklet,
        jobRepository: JobRepository,
        jpaTransactionManager: JpaTransactionManager
    ): Step =
        StepBuilder("syncOriginStep", jobRepository)
            .tasklet(syncOriginTasklet, jpaTransactionManager)
            .build()
}