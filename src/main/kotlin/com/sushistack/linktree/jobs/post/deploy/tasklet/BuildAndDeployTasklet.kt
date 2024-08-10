package com.sushistack.linktree.jobs.post.deploy.tasklet

import com.sushistack.linktree.entity.order.OrderStatus
import com.sushistack.linktree.entity.publisher.ServiceProviderType.CLOUD_BLOG_NETWORK
import com.sushistack.linktree.entity.publisher.ServiceProviderType.PRIVATE_BLOG_NETWORK
import com.sushistack.linktree.jobs.post.service.DeployService
import com.sushistack.linktree.jobs.post.service.JekyllService
import com.sushistack.linktree.service.OrderService
import com.sushistack.linktree.service.StaticWebpageService
import com.sushistack.linktree.utils.git.Git
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.ExitStatus
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

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val staticWebPages1 = staticWebpageService.findStaticWebpagesByProviderType(PRIVATE_BLOG_NETWORK)
        val staticWebPages2 = staticWebpageService.findStaticWebpagesByProviderType(CLOUD_BLOG_NETWORK)

        staticWebPages1.forEach {
            val repo = it.repository ?: throw NullPointerException("No repo to build")
            val git = Git(appHomeDir, repo.workspaceName, repo.repositoryName)
            try {
                jekyllService.build(git)
                deployService.makePackage(git)
                deployService.deploy(PRIVATE_BLOG_NETWORK, git, it.domain)
            } catch (e: Exception) {
                log.error(e) { "Failed to build and deploy [${repo.workspaceName}/${repo.repositoryName}]" }
                contribution.exitStatus = ExitStatus.FAILED
                return RepeatStatus.FINISHED
            }
        }

        staticWebPages2.forEach {
            val repo = it.repository ?: throw NullPointerException("No repo to build")
            val git = Git(appHomeDir, repo.workspaceName, repo.repositoryName)
            try {
                jekyllService.build(git)
                deployService.makePackage(git)
                deployService.deploy(CLOUD_BLOG_NETWORK, git, it.domain)
            } catch (e: Exception) {
                log.error(e) { "Failed to build and deploy [${repo.workspaceName}/${repo.repositoryName}]" }
                contribution.exitStatus = ExitStatus.FAILED
                return RepeatStatus.FINISHED
            }
        }

        val orders = orderService.findAllByOrderStatusOrderByOrderSeqDesc(OrderStatus.PROCESSED)
        orders.forEach { it.orderStatus = OrderStatus.next(it.orderStatus) }

        return RepeatStatus.FINISHED
    }
}