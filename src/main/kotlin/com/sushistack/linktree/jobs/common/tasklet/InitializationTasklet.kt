package com.sushistack.linktree.jobs.common.tasklet

import com.sushistack.linktree.service.InitializationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.stereotype.Component

@JobScope
@Component
class InitializationTasklet(private val initializationService: InitializationService): Tasklet {
    private val log = KotlinLogging.logger {}

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        initializationService.initialize()
        val jobInstanceId = chunkContext.stepContext.stepExecution.jobExecution.jobInstance.instanceId
        log.info { "jobInstanceId := $jobInstanceId" }
        contribution.stepExecution.jobExecution.executionContext.put("jobInstanceId", jobInstanceId)
        return RepeatStatus.FINISHED
    }
}