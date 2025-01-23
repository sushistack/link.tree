package com.sushistack.linktree.jobs.link.validation

import com.sushistack.linktree.batch.config.BatchJob.LINK_VALIDATION
import com.sushistack.linktree.jobs.common.listener.JobCompletionNotificationListener
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
@ConditionalOnProperty(value = ["spring.batch.job.name"], havingValue = "linkValidationJob")
class LinkValidationJobConfig {

    @Bean
    fun linkValidationJob(
        jobRepository: JobRepository,
        fixOrderStep: Step,
        linkValidationStep: Step,
        jobListener: JobCompletionNotificationListener
    ): Job =
        JobBuilder(LINK_VALIDATION.jobName, jobRepository)
            .start(fixOrderStep)
            .next(linkValidationStep)
            .listener(jobListener)
            .build()

    @Bean
    fun fixOrderStep(
        jobRepository: JobRepository,
        fixOrderTasklet: Tasklet,
        jpaTransactionManager: JpaTransactionManager
    ) =
        StepBuilder("fixOrderStep", jobRepository)
            .tasklet(fixOrderTasklet, jpaTransactionManager)
            .build()

    @Bean
    fun linkValidationStep(
        jobRepository: JobRepository,
        linkValidationTasklet: Tasklet,
        jpaTransactionManager: JpaTransactionManager
    ) =
        StepBuilder("linkValidationStep", jobRepository)
            .tasklet(linkValidationTasklet, jpaTransactionManager)
            .build()

}