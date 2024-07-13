package com.sushistack.linktree.jobs.link.validation

import com.sushistack.linktree.jobs.link.gen.listener.JobCompletionNotificationListener
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(value = ["spring.batch.job.name"], havingValue = "linkValidationJob")
class LinkValidationJobConfig {

    @Bean
    fun linkValidationJob(
        jobRepository: JobRepository,
        buildAndDeployStep: Step,
        jobListener: JobCompletionNotificationListener
    ): Job =
        JobBuilder("linkValidationJob", jobRepository)
            .start(buildAndDeployStep)
            .listener(jobListener)
            .build()

}