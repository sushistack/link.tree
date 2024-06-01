package com.sushistack.linktree.jobs.link.gen.report

import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.jobs.link.gen.report.model.LinkTable
import com.sushistack.linktree.service.LinkNodeService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@JobScope
@Component
class ReportTasklet(private val linkNodeService: LinkNodeService): Tasklet {

    private val log = KotlinLogging.logger {}

    @Value("#{jobExecutionContext['order']}")
    lateinit var order: Order

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val first = linkNodeService.findAllByOrderAndTier(order, tier = 1)
        val second = linkNodeService.findAllByOrderAndTier(order, tier = 2)
        val third = linkNodeService.findAllByOrderAndTier(order, tier = 3)

        log.info { "first tier := [${first.size}]" }
        log.info { "second tier := [${second.size}]" }
        log.info { "third tier := [${third.size}]" }

        return RepeatStatus.FINISHED
    }
}