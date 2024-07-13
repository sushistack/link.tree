package com.sushistack.linktree.jobs.link.validation.tasklet

import com.sushistack.linktree.entity.order.OrderStatus
import com.sushistack.linktree.service.LinkNodeService
import com.sushistack.linktree.service.OrderService
import com.sushistack.linktree.service.SlackNotificationService
import com.sushistack.linktree.service.ValidationService
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.stereotype.Component

@Component
class LinkValidationTasklet(
    private val orderService: OrderService,
    private val linkNodeService: LinkNodeService,
    private val validationService: ValidationService,
    private val slackNotificationService: SlackNotificationService
): Tasklet {

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val orderOpt = orderService.findTop1ByOrderStatusOrderByOrderSeqDesc(OrderStatus.DEPLOYED)
        if (!orderOpt.isPresent) {
            return RepeatStatus.FINISHED
        }

        val order = orderOpt.get()

        val linksOfTier1 = linkNodeService.findWithPostByOrder(order, tier = 1)
        val linksOfTier2 = linkNodeService.findWithPostByOrder(order, tier = 2)

        val statusOfLinks1 = linksOfTier1
            .map { it.url }
            .let { validationService.validatePosts(it).block() }
            ?.groupBy { it.statusCode } ?: emptyMap()

        val statusOfLinks2 = linksOfTier2
            .map { it.url }
            .let { validationService.validatePosts(it).block() }
            ?.groupBy { it.statusCode } ?: emptyMap()

        slackNotificationService.sendPostValidations(statusOfLinks1, statusOfLinks2)
        order.orderStatus = OrderStatus.next(order.orderStatus)

        return RepeatStatus.FINISHED
    }
}