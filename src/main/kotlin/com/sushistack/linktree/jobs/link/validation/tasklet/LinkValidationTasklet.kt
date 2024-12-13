package com.sushistack.linktree.jobs.link.validation.tasklet

import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.order.OrderStatus
import com.sushistack.linktree.service.LinkNodeService
import com.sushistack.linktree.service.OrderService
import com.sushistack.linktree.service.SlackNotificationService
import com.sushistack.linktree.service.ValidationService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@JobScope
@Component
class LinkValidationTasklet(
    private val orderService: OrderService,
    private val linkNodeService: LinkNodeService,
    private val validationService: ValidationService,
    private val slackNotificationService: SlackNotificationService
): Tasklet {

    private val log = KotlinLogging.logger {}

    @Value("#{jobExecutionContext['order']}")
    var order: Order? = null

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val o = order ?: orderService.findTop1ByOrderStatusOrderByOrderSeqDesc(OrderStatus.DEPLOYED).getOrNull()
        if (o == null) {
            log.info { "order is null" }
            return RepeatStatus.FINISHED
        }
        val linksOfTier1 = linkNodeService.findWithPostByOrder(o, tier = 1)

        val statusOfLinks1 = linksOfTier1
            .map { it.url }
            .let { validationService.validatePosts(it).block() }
            ?.groupBy { it.statusCode } ?: emptyMap()

        statusOfLinks1.forEach { entry -> log.info { "\n Code(${entry.key}): \n\n ${entry.value.joinToString("\n")}" } }

        slackNotificationService.sendPostValidations(statusOfLinks1)
        o.orderStatus = OrderStatus.next(o.orderStatus)

        return RepeatStatus.FINISHED
    }
}