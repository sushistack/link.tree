package com.sushistack.linkstacker.config

import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaTransactionManager

@Configuration
class BatchConfig {

    @Bean
    fun transactionManager(entityManagerFactory: EntityManagerFactory): JpaTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }

    @Bean
    fun jobExecutionListener(): JobExecutionListener {
        return object : JobExecutionListener {
            override fun beforeJob(jobExecution: JobExecution) {
                println("Job is about to start: ${jobExecution.jobInstance.jobName}")
            }

            override fun afterJob(jobExecution: JobExecution) {
                println("Job has finished: ${jobExecution.jobInstance.jobName}")
            }
        }
    }
}