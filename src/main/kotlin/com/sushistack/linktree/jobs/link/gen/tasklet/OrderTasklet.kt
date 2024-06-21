package com.sushistack.linktree.jobs.link.gen.tasklet

import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.order.OrderType
import com.sushistack.linktree.jobs.link.gen.service.LinkProvider
import com.sushistack.linktree.service.ArticleService
import com.sushistack.linktree.service.OrderService
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
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
    @Value("#{jobParameters['anchorTexts']}") private val anchorTextsJson: String,
    @Value("#{jobParameters['keywords']}") private val keywordsJson: String,
    private val orderService: OrderService,
    private val articleService: ArticleService
): Tasklet {

    private val log = KotlinLogging.logger {}

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

        val anchorTexts: List<String> = Json.decodeFromString(anchorTextsJson)
        contribution.stepExecution.jobExecution.executionContext.put("anchorTexts", anchorTexts)

        val linkProvider = LinkProvider(targetUrl, anchorTexts)
        contribution.stepExecution.jobExecution.executionContext.put("linkProvider", linkProvider)

        val keywords: List<String> = Json.decodeFromString(keywordsJson)
        val articleSources = keywords.flatMap { articleService.getArticleSources(it) }
        contribution.stepExecution.jobExecution.executionContext.put("articleSources", articleSources)
        log.info { "ArticleSources size := [${articleSources.size}]" }

        return RepeatStatus.FINISHED
    }
}