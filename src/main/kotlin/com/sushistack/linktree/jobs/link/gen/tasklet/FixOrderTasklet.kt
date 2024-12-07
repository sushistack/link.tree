package com.sushistack.linktree.jobs.link.gen.tasklet

import com.sushistack.linktree.service.OrderService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.stereotype.Component

@JobScope
@Component
class FixOrderTasklet(
    private val orderService: OrderService,
): Tasklet {

    private val log = KotlinLogging.logger {}

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val order = orderService.getOrderBySeq(1)

        contribution.stepExecution.jobExecution.executionContext.put("order", order)
        log.info { "Fixed Order := [${order}]" }

        return RepeatStatus.FINISHED
    }
}