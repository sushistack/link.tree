package com.sushistack.linktree

import com.sushistack.linktree.batch.config.BatchJob
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class JobRunner(private val jobLauncher: JobLauncher, private val job: Job): CommandLineRunner {
    private val log = KotlinLogging.logger {}


    override fun run(vararg args: String?) {
        val paramMap = getJobParameterMap(args)

        val builder = JobParametersBuilder()
        paramMap.forEach { (key, value) -> builder.addString(key, value) }

        if (BatchJob.getIdemponentJobs().contains(job.name)) {
            builder.addString("execDate", LocalDateTime.now().toString())
        }

        val jobParameters = builder.toJobParameters()
        log.info { "jobParameters = $jobParameters" }

        val jobExecution = jobLauncher.run(job, jobParameters)
        log.info { "Job Status: ${jobExecution.status}" }
    }

    private fun getJobParameterMap(args: Array<out String?>) =
        args.filterNotNull()
            .associate {
                val (key, value) = it.split("=")
                key to value
            }
            .filter { !it.key.startsWith("--") }
            .toMap()
}