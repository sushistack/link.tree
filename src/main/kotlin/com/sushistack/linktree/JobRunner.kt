package com.sushistack.linktree

import com.sushistack.linktree.entity.order.OrderType
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class JobRunner {
    private val log = KotlinLogging.logger {}

    @Bean
    fun runJob(jobLauncher: JobLauncher, job: Job) = CommandLineRunner {
        val jobParameters = JobParametersBuilder()
            .addLong("runTime", System.currentTimeMillis())
            .addString("orderType", OrderType.DELUXE.name)
            .addString("targetUrl", "https://test.com")
            .addString("customerName", "고객명")
            .toJobParameters()

        // val jobExecution = jobLauncher.run(job, jobParameters)
        // log.info { "Job Status: ${jobExecution.status}" }
    }


}