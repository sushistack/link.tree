package com.sushistack.linktree.jobs.link.gen

import com.sushistack.linktree.jobs.link.gen.order.OrderTasklet
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.TaskletStep
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaTransactionManager

@Configuration
class LinkGenerationJobConfig {

    @Bean
    fun linkGenerationJob(
        jobRepository: JobRepository,
        saveOrderStep: Step
    ): Job =
        JobBuilder("linkGenerationJob", jobRepository)
            .start(saveOrderStep)
            .build()

    @Bean
    fun saveOrderStep(
        jobRepository: JobRepository,
        orderTasklet: OrderTasklet,
        jpaTransactionManager: JpaTransactionManager,
    ): TaskletStep =
        StepBuilder("saveOrderStep", jobRepository)
            .tasklet(orderTasklet, jpaTransactionManager)
            .build()

}