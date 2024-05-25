package com.sushistack.linktree.jobs.link.gen.report

import com.sushistack.linktree.entity.order.Order
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@JobScope
@Component
class ReportTasklet: Tasklet {

    @Value("#{jobExecutionContext['order']}")
    lateinit var order: Order

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        TODO("Not yet implemented")
    }
}