package com.sushistack.linktree

import com.sushistack.linktree.entity.order.OrderType
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class JobRunner(private val jobLauncher: JobLauncher, private val job: Job): CommandLineRunner {
    private val log = KotlinLogging.logger {}

    override fun run(vararg args: String?) {
        val paramMap = getJobParameterMap(args)

        log.info { "paramMap: $paramMap" }

        val jobParameters = JobParametersBuilder()
            .addString("orderType", paramMap["orderType"] ?: OrderType.DELUXE.name)
            .addString("targetUrl", paramMap["targetUrl"] ?: "https://test.com")
            .addString("customerName", paramMap["customerName"] ?: "고객명21")
            .addString("anchorTexts", paramMap["anchorTexts"] ?: Json.encodeToString(listOf("감자", "감자맨")))
            .addString("keywords", paramMap["keywords"] ?: Json.encodeToString(listOf("감자의 효능")))
            .toJobParameters()

        val jobExecution = jobLauncher.run(job, jobParameters)
        log.info { "Job Status: ${jobExecution.status}" }
    }

    private fun getJobParameterMap(args: Array<out String?>) =
        args.filterNotNull()
            .associate {
                val (key, value) = it.split("=")
                key to value
            }.toMap()
}