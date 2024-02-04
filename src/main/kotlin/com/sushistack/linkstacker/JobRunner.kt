package com.sushistack.linkstacker

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobParametersBuilder
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class JobRunner {
    @Bean
    fun runJob(jobLauncher: JobLauncher, job: Job): CommandLineRunner {
        return CommandLineRunner {
            // JobParameters 생성
            val jobParameters = JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters()

            // Job 실행
            val jobExecution = jobLauncher.run(job, jobParameters)
            println("Job Status: ${jobExecution.status}")
        }
    }
}