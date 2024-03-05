package com.sushistack.linktree.config

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaTransactionManager

@Configuration
class BatchConfig {

    val log = KotlinLogging.logger {}

    @Bean
    fun transactionManager(entityManagerFactory: EntityManagerFactory): JpaTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }

    @Bean
    fun jobExecutionListener(): JobExecutionListener {
        return object : JobExecutionListener {
            override fun beforeJob(jobExecution: JobExecution) {
                log.info { "Job is about to start: ${jobExecution.jobInstance.jobName}" }
            }

            override fun afterJob(jobExecution: JobExecution) {
                log.info { "Job has finished: ${jobExecution.jobInstance.jobName}" }
            }
        }
    }
}