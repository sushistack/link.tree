package com.sushistack.linktree.jobs.link.validation

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
@ConditionalOnProperty(value = ["spring.batch.job.name"], havingValue = "linkValidationJob")
class LinkValidationJobConfig {

    @Bean
    fun linkValidationJob(
        jobRepository: JobRepository,
        linkValidationStep: Step,
        jobListener: JobCompletionNotificationListener
    ): Job =
        JobBuilder("linkValidationJob", jobRepository)
            .start(linkValidationStep)
            .listener(jobListener)
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