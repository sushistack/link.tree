package com.sushistack.linktree.jobs.post.deploy

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
@ConditionalOnProperty(value = ["spring.batch.job.name"], havingValue = "postDeployJob")
class PostDeployConfig {

    @Bean
    fun postDeployJob(
        jobRepository: JobRepository,
        buildAndDeployStep: Step,
        initializationStep: Step,
        clearingInitializationStep: Step,
        jobListener: JobCompletionNotificationListener
    ): Job =
        JobBuilder("postDeployJob", jobRepository)
            .start(buildAndDeployStep)
            .listener(jobListener)
            .build()

    @Bean
    fun buildAndDeployStep(
        jobRepository: JobRepository,
        buildAndDeployTasklet: Tasklet,
        jpaTransactionManager: JpaTransactionManager
    ) =
        StepBuilder("buildAndDeployStep", jobRepository)
            .tasklet(buildAndDeployTasklet, jpaTransactionManager)
            .build()
}