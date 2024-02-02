package com.sushistack.linkstacker.config

import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableBatchProcessing
class BatchConfig {
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