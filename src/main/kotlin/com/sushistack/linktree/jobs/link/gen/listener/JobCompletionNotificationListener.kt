package com.sushistack.linktree.jobs.link.gen.listener

import com.sushistack.linktree.service.JobDetail
import com.sushistack.linktree.service.SlackNotificationService
import com.sushistack.linktree.service.StepDetail
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobExecutionListener
import org.springframework.stereotype.Component

@Component
class JobCompletionNotificationListener(
    private val slackNotificationService: SlackNotificationService
): JobExecutionListener {

    override fun afterJob(jobExecution: JobExecution) {
        val message = when (jobExecution.status.isUnsuccessful) {
            true -> "Some steps failed during the job execution."
            false -> "All steps completed successfully."
        }

        val jobDetail = JobDetail(
            name = jobExecution.jobInstance.jobName,
            status = jobExecution.status.name,
            startTime = jobExecution.startTime?.toString() ?: "N/A",
            endTime = jobExecution.endTime?.toString() ?: "N/A",
            message = message
        )

        val stepDetails = jobExecution.stepExecutions.map {
            StepDetail(
                name = it.stepName,
                status = it.status.name,
                startTime = it.startTime?.toString() ?: "N/A",
                endTime = it.endTime?.toString() ?: "N/A"
            )
        }

        slackNotificationService.sendJobDetail(jobDetail, stepDetails)
    }
}