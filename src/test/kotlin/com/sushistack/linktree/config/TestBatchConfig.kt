package com.sushistack.linktree.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.orm.jpa.JpaTransactionManager

@Configuration
class BatchConfig {
    private val log = KotlinLogging.logger {}

    @Bean
    @Primary
    fun testJob(
        jobRepository: JobRepository,
        testStep: Step
    ): Job =
        JobBuilder("testJob", jobRepository)
            .start(testStep)
            .build()

    @Bean
    fun testStep(
        jobRepository: JobRepository,
        jpaTransactionManager: JpaTransactionManager
    ): Step =
        StepBuilder("testStep", jobRepository)
            .tasklet(testTasklet(), jpaTransactionManager)
            .build()

    @Bean
    fun testTasklet(): Tasklet =
        Tasklet { _, _ ->
            log.info { "Executing test step in TestBatchConfig" }
            RepeatStatus.FINISHED
        }
}