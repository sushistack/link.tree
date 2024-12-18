package com.sushistack.linktree.jobs.post.deploy.tasklet

import com.sushistack.linktree.entity.order.OrderStatus
import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.entity.publisher.ServiceProviderType.CLOUD_BLOG_NETWORK
import com.sushistack.linktree.entity.publisher.ServiceProviderType.PRIVATE_BLOG_NETWORK
import com.sushistack.linktree.jobs.post.service.DeployService
import com.sushistack.linktree.jobs.post.service.DeployService.SimpleGitRepository
import com.sushistack.linktree.jobs.post.service.JekyllService
import com.sushistack.linktree.service.LinkNodeService
import com.sushistack.linktree.service.OrderService
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
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
    private val log = KotlinLogging.logger {}

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val orderOpt = orderService.findTop1ByOrderStatusOrderByOrderSeqDesc(OrderStatus.DONE)
        if (!orderOpt.isPresent) {
            return RepeatStatus.FINISHED
        }

        val order = orderOpt.get()

        val linksOfTier1 = linkNodeService.findWithPostByOrder(order, tier = 1)
        val linksOfTier2 = linkNodeService.findWithPostByOrder(order, tier = 2)

        runBlocking {
            linksOfTier1.forEach { linkNode ->
                jekyllService.build(linkNode.workspaceName, linkNode.repositoryName)
                val repo = SimpleGitRepository(linkNode.workspaceName, linkNode.repositoryName, linkNode.username, linkNode.appPassword)
                deployService.makePackage(repo)
                deployService.deploy(PRIVATE_BLOG_NETWORK, repo)
            }
        }


        runBlocking {
            val jobs1 = async {
                linksOfTier1.map { linkNode ->
                    async {
                        jekyllService.build(linkNode.workspaceName, linkNode.repositoryName)
                        val repo = SimpleGitRepository(linkNode.workspaceName, linkNode.repositoryName, linkNode.username, linkNode.appPassword)
                        deployService.makePackage(repo)
                        deployService.deploy(PRIVATE_BLOG_NETWORK, repo)
                    }
                }.awaitAll()
            }

            val jobs2 = async {
                linksOfTier2.map { linkNode ->
                    async {
                        jekyllService.build(linkNode.workspaceName, linkNode.repositoryName)
                        val repo = SimpleGitRepository(linkNode.workspaceName, linkNode.repositoryName, linkNode.username, linkNode.appPassword)
                        deployService.makePackage(repo)
                        deployService.deploy(CLOUD_BLOG_NETWORK, repo)
                    }
                }.awaitAll()
            }

            jobs1.await()
            jobs2.await()
        }

        return RepeatStatus.FINISHED
    }
}