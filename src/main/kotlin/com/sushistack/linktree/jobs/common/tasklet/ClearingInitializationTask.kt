package com.sushistack.linktree.jobs.common.tasklet

import com.sushistack.linktree.service.InitializationService
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.stereotype.Component

@Component
class ClearingInitializationTask(private val initializationService: InitializationService): Tasklet {

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        initializationService.clearAll()
        return RepeatStatus.FINISHED
    }
}