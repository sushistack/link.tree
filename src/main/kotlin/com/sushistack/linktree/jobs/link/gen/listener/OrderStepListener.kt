package com.sushistack.linktree.jobs.link.gen.listener

import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.order.OrderStatus
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.StepExecution
import org.springframework.batch.core.StepExecutionListener
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@JobScope
@Component
class OrderStepListener: StepExecutionListener {
    private val log = KotlinLogging.logger {}

    @Value("#{jobExecutionContext['order']}")
    lateinit var order: Order

    override fun afterStep(stepExecution: StepExecution): ExitStatus {
        order.orderStatus = OrderStatus.next(order.orderStatus)
        log.info { "${stepExecution.stepName} is Completed, Order(${order.orderStatus})" }
        return stepExecution.exitStatus;
    }
}