package com.sushistack.linktree.jobs.post.deploy.tasklet

import com.sushistack.linktree.entity.order.OrderStatus
import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.entity.publisher.ServiceProviderType.CLOUD_BLOG_NETWORK
import com.sushistack.linktree.entity.publisher.ServiceProviderType.PRIVATE_BLOG_NETWORK
import com.sushistack.linktree.jobs.post.service.DeployService
import com.sushistack.linktree.jobs.post.service.JekyllService
import com.sushistack.linktree.service.OrderService
import com.sushistack.linktree.service.StaticWebpageService
import com.sushistack.linktree.utils.git.Git
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.stereotype.Component

@JobScope
@Component
class BuildAndDeployTasklet(
    private val appHomeDir: String,
    private val orderService: OrderService,
    private val jekyllService: JekyllService,
    private val deployService: DeployService,
    private val staticWebpageService: StaticWebpageService
) : Tasklet {
    private val log = KotlinLogging.logger {}

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? = runBlocking {
        val staticWebPages1 = staticWebpageService.findStaticWebpagesByProviderType(PRIVATE_BLOG_NETWORK)
        val staticWebPages2 = staticWebpageService.findStaticWebpagesByProviderType(CLOUD_BLOG_NETWORK)

        staticWebPages1
            .filter { it.repository != null }
            .map {
                async {
                    buildAndDeployAsync(
                        Git(appHomeDir, it.repository!!.workspaceName, it.repository!!.repositoryName),
                        PRIVATE_BLOG_NETWORK,
                        it.domain
                    )
                }
            }.awaitAll()

        staticWebPages2
            .filter { it.repository != null }
            .map {
                async {
                    buildAndDeployAsync(
                        Git(appHomeDir, it.repository!!.workspaceName, it.repository!!.repositoryName),
                        CLOUD_BLOG_NETWORK,
                        it.domain
                    )
                }
            }.awaitAll()

        val orders = orderService.findByOrderStatus(OrderStatus.PROCESSED)
        orders.forEach { it.orderStatus = OrderStatus.next(it.orderStatus) }

        RepeatStatus.FINISHED
    }

    suspend fun buildAndDeployAsync(git: Git, providerType: ServiceProviderType, domain: String) = withContext(Dispatchers.IO) {
        log.info { "build and deploy for (${git.workspaceName}/${git.repositoryName}), domain: $domain" }
        jekyllService.build(git)
        deployService.makePackage(git)
        deployService.deploy(providerType, git, domain)
    }
}