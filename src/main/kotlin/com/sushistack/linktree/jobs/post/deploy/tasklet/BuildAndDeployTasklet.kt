package com.sushistack.linktree.jobs.post.deploy.tasklet

import com.sushistack.linktree.entity.order.OrderStatus
import com.sushistack.linktree.entity.publisher.ServiceProviderType.CLOUD_BLOG_NETWORK
import com.sushistack.linktree.entity.publisher.ServiceProviderType.PRIVATE_BLOG_NETWORK
import com.sushistack.linktree.jobs.post.service.DeployService
import com.sushistack.linktree.jobs.post.service.DeployService.SimpleGitRepository
import com.sushistack.linktree.jobs.post.service.JekyllService
import com.sushistack.linktree.service.LinkNodeService
import com.sushistack.linktree.service.OrderService
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
    private val orderService: OrderService,
    private val jekyllService: JekyllService,
    private val deployService: DeployService,
    private val linkNodeService: LinkNodeService
) : Tasklet {

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val orderOpt = orderService.findTop1ByOrderStatusOrderByOrderSeqDesc(OrderStatus.PROCCESSED)
        if (!orderOpt.isPresent) {
            return RepeatStatus.FINISHED
        }

        val order = orderOpt.get()

        val linksOfTier1 = linkNodeService.findWithPostByOrder(order, tier = 1)
        val linksOfTier2 = linkNodeService.findWithPostByOrder(order, tier = 2)

        linksOfTier1.forEach { linkNode ->
            jekyllService.build(linkNode.workspaceName, linkNode.repositoryName, linkNode.appPassword)
            val repo = SimpleGitRepository(linkNode.workspaceName, linkNode.repositoryName, linkNode.domain, linkNode.username, linkNode.appPassword)
            deployService.makePackage(repo)
            deployService.deploy(PRIVATE_BLOG_NETWORK, repo)
        }

        linksOfTier2.forEach { linkNode ->
            jekyllService.build(linkNode.workspaceName, linkNode.repositoryName, linkNode.appPassword)
            val repo = SimpleGitRepository(linkNode.workspaceName, linkNode.repositoryName, linkNode.domain, linkNode.username, linkNode.appPassword)
            deployService.makePackage(repo)
            deployService.deploy(CLOUD_BLOG_NETWORK, repo)
        }

        order.orderStatus = OrderStatus.next(order.orderStatus)

        return RepeatStatus.FINISHED
    }
}