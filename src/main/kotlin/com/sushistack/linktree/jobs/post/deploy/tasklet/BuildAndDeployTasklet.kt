package com.sushistack.linktree.jobs.post.deploy.tasklet

import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.order.OrderStatus
import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.entity.publisher.ServiceProviderType.CLOUD_BLOG_NETWORK
import com.sushistack.linktree.entity.publisher.ServiceProviderType.PRIVATE_BLOG_NETWORK
import com.sushistack.linktree.entity.publisher.StaticWebpage
import com.sushistack.linktree.jobs.post.service.DeployService
import com.sushistack.linktree.jobs.post.service.JekyllService
import com.sushistack.linktree.service.OrderService
import com.sushistack.linktree.service.StaticWebpageService
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@JobScope
@Component
class BuildAndDeployTasklet(
    @Value("#{jobParameters['skipCbn']}") private val skipCbn: String,
    private val orderService: OrderService,
    private val jekyllService: JekyllService,
    private val deployService: DeployService,
    private val staticWebpageService: StaticWebpageService,
    private val pbnDispatcher: ExecutorCoroutineDispatcher,
    private val cbnDispatcher: ExecutorCoroutineDispatcher
) : Tasklet {
    private val log = KotlinLogging.logger {}

    companion object {
        private val coroutineTimeout: (ServiceProviderType) -> Long = { providerType ->
            when(providerType) {
                PRIVATE_BLOG_NETWORK -> 150_000L
                CLOUD_BLOG_NETWORK -> 270_000L
                else -> 15_000L
            }
        }
    }

    @Value("#{jobExecutionContext['order']}")
    var order: Order? = null

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        log.info { "order := $order skipCbn := $skipCbn" }
        val staticWebPages1 = getPrivateBlogNetworks(order)
        val staticWebPages2 = getCloudBlogNetworks(skipCbn)

        log.info { "staticWebPage1 = ${staticWebPages1.size}, staticWebPage2 = ${staticWebPages2.size}" }
        log.info { "### Start to Build and Deploy Async ###" }
        runBlocking {
            val jobs = (staticWebPages1 + staticWebPages2).map { webpage ->
                val timeout = coroutineTimeout(webpage.providerType)
                launch (getDispatcher(webpage.providerType)) {
                    try {
                        withTimeout(timeout) { buildAndDeployAsync(webpage) }
                    } catch (e: TimeoutCancellationException) {
                        log.error(e) { "Timed out of build and deploy, webpage := $webpage" }
                    } catch (e: Exception) {
                        log.error(e) { "Unexpected error during build and deploy, webpage: $webpage" }
                    }
                }
            }
            jobs.forEach { it.join() }
        }

        pbnDispatcher.close()
        cbnDispatcher.close()

        log.info { "### Ended to Build and Deploy Async ###" }
        val orders = orderService.findByOrderStatus(OrderStatus.PROCESSED)
        orders.forEach { it.orderStatus = OrderStatus.next(it.orderStatus) }

        return RepeatStatus.FINISHED
    }

    suspend fun buildAndDeployAsync(webpage: StaticWebpage) {
        jekyllService.build(webpage)
        deployService.makePackage(webpage)
        deployService.deploy(webpage)
    }

    private fun getPrivateBlogNetworks(order: Order?): List<StaticWebpage> = when(order == null) {
        true -> staticWebpageService.findStaticWebpagesByProviderType(PRIVATE_BLOG_NETWORK)
        false -> staticWebpageService.findStaticWebpagesByOrderAndProviderType(order, PRIVATE_BLOG_NETWORK)
    }

    private fun getCloudBlogNetworks(skipCbn: String): List<StaticWebpage> = when(skipCbn) {
        "true" -> emptyList()
        else -> staticWebpageService.findStaticWebpagesByProviderType(CLOUD_BLOG_NETWORK)
    }

    private fun getDispatcher(providerType: ServiceProviderType) = when (providerType) {
        PRIVATE_BLOG_NETWORK -> pbnDispatcher
        CLOUD_BLOG_NETWORK -> cbnDispatcher
        else -> Dispatchers.IO
    }
}