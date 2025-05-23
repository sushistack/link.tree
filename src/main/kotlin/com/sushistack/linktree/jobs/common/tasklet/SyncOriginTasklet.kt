package com.sushistack.linktree.jobs.common.tasklet

import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.publisher.ServiceProviderType.CLOUD_BLOG_NETWORK
import com.sushistack.linktree.entity.publisher.ServiceProviderType.PRIVATE_BLOG_NETWORK
import com.sushistack.linktree.service.LinkNodeService
import com.sushistack.linktree.service.StaticWebpageService
import com.sushistack.linktree.utils.git.Git
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@JobScope
@Component
class SyncOriginTasklet(
    private val appHomeDir: String,
    private val linkNodeService: LinkNodeService,
    private val staticWebpageService: StaticWebpageService
) : Tasklet {
    private val log = KotlinLogging.logger {}

    @Value("#{jobExecutionContext['order']}")
    var order: Order? = null

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? = runBlocking {
        val gitsOfTier1 = getGitOfTier1LinkNodes()

        log.info { "sync first tiers, size := ${gitsOfTier1.size}" }
        gitsOfTier1.map { git ->
            async {
                log.info { "sync: push to (${git.workspaceName}/${git.repositoryName})" }
                git.pushAsync(Git.DEFAULT_BRANCH)
            }
        }.awaitAll()

        val gitsOfTier2 = staticWebpageService.findStaticWebpagesByProviderType(CLOUD_BLOG_NETWORK)
            .mapNotNull { it.repository }
            .map { Git(appHomeDir, it.workspaceName, it.repositoryName) }

        log.info { "sync second tiers, size := ${gitsOfTier2.size}" }
        gitsOfTier2.map { git ->
            async {
                log.info { "sync: push to (${git.workspaceName}/${git.repositoryName})" }
                git.pushAsync(Git.DEFAULT_BRANCH)
            }
        }.awaitAll()

        RepeatStatus.FINISHED
    }

    private fun getGitOfTier1LinkNodes(): List<Git> {
        if (order == null) {
            return staticWebpageService.findStaticWebpagesByProviderType(PRIVATE_BLOG_NETWORK)
                .mapNotNull { it.repository }
                .map { g -> Git(appHomeDir, g.workspaceName, g.repositoryName) }
                .distinct()
        }

        return linkNodeService.findWithPostByOrder(order!!, tier = 1)
            .map { g -> Git(appHomeDir, g.workspaceName, g.repositoryName) }
            .distinct()
    }
}