package com.sushistack.linktree.jobs.crawl

import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaTransactionManager

@Configuration
class CrawlConfig {

    @Bean
    fun crawlJob(
        jobRepository: JobRepository,
        crawlStep: Step,
    ): Job =
        JobBuilder("crawlJob", jobRepository)
            .start(crawlStep)
            .build()

    @Bean
    fun crawlStep(
        jobRepository: JobRepository,
        crawlTasklet: Tasklet,
        jpaTransactionManager: JpaTransactionManager
    ) =
        StepBuilder("crawlStep", jobRepository)
            .tasklet(crawlTasklet, jpaTransactionManager)
            .build()

}