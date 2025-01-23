package com.sushistack.linktree.jobs.post.gen

import com.sushistack.linktree.batch.config.BatchJob.POST_GENERATION
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
@ConditionalOnProperty(value = ["spring.batch.job.name"], havingValue = "postGenerationJob")
class PostGenerationJobConfig {

    @Bean
    fun postGenerationJob(
        jobRepository: JobRepository,
        postGenerationStep: Step,
        jobListener: JobCompletionNotificationListener
    ): Job =
        JobBuilder(POST_GENERATION.jobName, jobRepository)
            .start(postGenerationStep)
            .listener(jobListener)
            .build()

    @Bean
    fun postGenerationStep(
        jobRepository: JobRepository,
        postGenerationTasklet: Tasklet,
        jpaTransactionManager: JpaTransactionManager
    ): Step =
        StepBuilder("postGenerationStep", jobRepository)
            .tasklet(postGenerationTasklet, jpaTransactionManager)
            .build()
}