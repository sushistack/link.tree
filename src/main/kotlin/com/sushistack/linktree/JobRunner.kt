package com.sushistack.linktree

import com.sushistack.linktree.entity.order.OrderType
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class JobRunner {
    private val log = KotlinLogging.logger {}

    @Bean
    fun runJob(jobLauncher: JobLauncher, job: Job) = CommandLineRunner {
        val jobParameters = JobParametersBuilder()
            .addString("orderType", OrderType.DELUXE.name)
            .addString("targetUrl", "https://test.com")
            .addString("customerName", "고객명14")
            .addString("anchorTexts", Json.encodeToString(listOf("감자", "감자맨")))
            .addString("keywords", Json.encodeToString(listOf("감자의 효능")))
            .toJobParameters()

         val jobExecution = jobLauncher.run(job, jobParameters)
         log.info { "Job Status: ${jobExecution.status}" }
    }
}