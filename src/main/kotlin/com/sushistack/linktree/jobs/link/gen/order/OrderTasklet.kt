package com.sushistack.linktree.jobs.link.gen.order

import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.order.OrderType
import com.sushistack.linktree.repository.order.OrderRepository
import com.sushistack.linktree.service.OrderService
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
class OrderTasklet(
    @Value("#{jobParameters['orderType']}") private val orderType: String,
    @Value("#{jobParameters['targetUrl']}") private val targetUrl: String,
    @Value("#{jobParameters['customerName']}") private val customerName: String,
    private val orderService: OrderService
): Tasklet {

    val log = KotlinLogging.logger {}

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val order = orderService.createOrder(
            Order(
                orderType = OrderType.valueOf(orderType),
                targetUrl = targetUrl,
                customerName = customerName
            )
        )
        contribution.stepExecution.jobExecution.executionContext.put("order", order)
        log.info { "Saved Order := [${order}]" }

        return RepeatStatus.FINISHED
    }
}