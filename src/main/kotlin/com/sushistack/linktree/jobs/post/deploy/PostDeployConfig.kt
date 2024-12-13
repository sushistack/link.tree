package com.sushistack.linktree.jobs.post.deploy

import com.sushistack.linktree.batch.config.BatchJob.POST_DEPLOY
import com.sushistack.linktree.jobs.link.gen.listener.JobCompletionNotificationListener
import com.sushistack.linktree.jobs.link.gen.tasklet.ClearingInitializationTask
import com.sushistack.linktree.jobs.link.gen.tasklet.InitializationTasklet
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.core.step.tasklet.TaskletStep
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
        syncOriginStep: Step,
        buildAndDeployStep: Step,
        initializationStep: Step,
        clearingInitializationStep: Step,
        jobListener: JobCompletionNotificationListener
    ): Job =
        JobBuilder(POST_DEPLOY.jobName, jobRepository)
//            .start(clearingInitializationStep)
//            .next(initializationStep)
            .start(syncOriginStep)
            .next(buildAndDeployStep)
            .listener(jobListener)
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
    fun clearingInitializationStep(
        jobRepository: JobRepository,
        clearingInitializationTask: ClearingInitializationTask,
        jpaTransactionManager: JpaTransactionManager
    ): TaskletStep =
        StepBuilder("clearingInitializationStep", jobRepository)
            .tasklet(clearingInitializationTask, jpaTransactionManager)
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
    fun buildAndDeployStep(
        jobRepository: JobRepository,
        buildAndDeployTasklet: Tasklet,
        jpaTransactionManager: JpaTransactionManager
    ) =
        StepBuilder("buildAndDeployStep", jobRepository)
            .tasklet(buildAndDeployTasklet, jpaTransactionManager)
            .build()
}