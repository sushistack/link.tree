package com.sushistack.linktree.jobs.post.deploy

import com.sushistack.linktree.batch.config.BatchJob.POST_DEPLOY
import com.sushistack.linktree.jobs.link.gen.listener.JobCompletionNotificationListener
import io.github.oshai.kotlinlogging.KotlinLogging
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
        fixOrderStep: Step,
        syncOriginStep: Step,
        buildAndDeployStep: Step,
        jobRepository: JobRepository,
        jobListener: JobCompletionNotificationListener
    ): Job =
        JobBuilder(POST_DEPLOY.jobName, jobRepository)
            .start(fixOrderStep)
            .next(syncOriginStep)
            .next(buildAndDeployStep)
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
    fun syncOriginStep(
        syncOriginTasklet: Tasklet,
        jobRepository: JobRepository,
        jpaTransactionManager: JpaTransactionManager
    ): Step =
        StepBuilder("syncOriginStep", jobRepository)
            .tasklet(syncOriginTasklet, jpaTransactionManager)
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