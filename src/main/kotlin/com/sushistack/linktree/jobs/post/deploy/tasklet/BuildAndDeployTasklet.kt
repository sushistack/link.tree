package com.sushistack.linktree.jobs.post.deploy.tasklet

import com.sushistack.linktree.entity.order.OrderStatus
import com.sushistack.linktree.entity.publisher.ServiceProviderType.CLOUD_BLOG_NETWORK
import com.sushistack.linktree.entity.publisher.ServiceProviderType.PRIVATE_BLOG_NETWORK
import com.sushistack.linktree.jobs.post.service.DeployService
import com.sushistack.linktree.jobs.post.service.JekyllService
import com.sushistack.linktree.service.LinkNodeService
import com.sushistack.linktree.service.OrderService
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
    private val linkNodeService: LinkNodeService
) : Tasklet {
    private val log = KotlinLogging.logger {}

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val orderOpt = orderService.findTop1ByOrderStatusOrderByOrderSeqDesc(OrderStatus.PROCCESSED)
        if (!orderOpt.isPresent) {
            return RepeatStatus.FINISHED
        }

        val order = orderOpt.get()

        val linksOfTier1 = linkNodeService.findWithPostByOrder(order, tier = 1)
        val linksOfTier2 = linkNodeService.findWithPostByOrder(order, tier = 2)

        linksOfTier1.forEach { linkNode ->
            val git = Git(appHomeDir, linkNode.workspaceName, linkNode.repositoryName)
            try {
                jekyllService.build(git)
                deployService.makePackage(git)
                deployService.deploy(PRIVATE_BLOG_NETWORK, git, linkNode.domain)
            } catch (e: Exception) {
                log.error(e) { "Failed to build and deploy [${linkNode.workspaceName}/${linkNode.repositoryName}]" }
                contribution.exitStatus = ExitStatus.FAILED
                return RepeatStatus.FINISHED
            }
        }

        linksOfTier2.forEach { linkNode ->
            val git = Git(appHomeDir, linkNode.workspaceName, linkNode.repositoryName)
            try {
                jekyllService.build(git)
                deployService.makePackage(git)
                deployService.deploy(CLOUD_BLOG_NETWORK, git, linkNode.domain)
            } catch (e: Exception) {
                log.error(e) { "Failed to build and deploy [${linkNode.workspaceName}/${linkNode.repositoryName}]" }
                contribution.exitStatus = ExitStatus.FAILED
                return RepeatStatus.FINISHED
            }
        }

        order.orderStatus = OrderStatus.next(order.orderStatus)

        return RepeatStatus.FINISHED
    }
}