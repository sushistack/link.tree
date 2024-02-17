package com.sushistack.linkstacker.jobs

import com.sushistack.linkstacker.log
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaTransactionManager

@Configuration
class SampleJobConfig{

    @Bean
    fun simpleJob1(
        jobRepository: JobRepository,
        simpleStep1: Step
    ): Job {
        return JobBuilder("simpleJob", jobRepository)
            .start(simpleStep1)
            .build()
    }

    @Bean
    fun simpleStep1(
        jobRepository: JobRepository,
        testTasklet: Tasklet,
        jpaTransactionManager: JpaTransactionManager,
    ): Step = StepBuilder("simpleStep1", jobRepository)
        .tasklet(testTasklet, jpaTransactionManager).build()

    @Bean
    fun testTasklet() = Tasklet { _, _ ->
        log.info { "####" }

        RepeatStatus.FINISHED
    }
}