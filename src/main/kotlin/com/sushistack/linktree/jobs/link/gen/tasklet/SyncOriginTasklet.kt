package com.sushistack.linktree.jobs.link.gen.tasklet

import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.publisher.ServiceProviderType.CLOUD_BLOG_NETWORK
import com.sushistack.linktree.service.LinkNodeService
import com.sushistack.linktree.service.StaticWebpageService
import com.sushistack.linktree.utils.git.Git
import io.github.oshai.kotlinlogging.KotlinLogging
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
    lateinit var order: Order

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val gitsOfTier1 = linkNodeService.findWithPostByOrder(order, tier = 1)
            .map { Git(appHomeDir, it.workspaceName, it.repositoryName) }
            .distinct()

        log.info { "sync first tiers, size := ${gitsOfTier1.size}" }
        gitsOfTier1.forEach { git ->
                log.info { "sync: push to (${git.workspaceName}/${git.repositoryName})" }
                git.push(Git.DEFAULT_BRANCH)
            }

        val gitsOfTier2 = staticWebpageService.findStaticWebpagesByProviderType(CLOUD_BLOG_NETWORK)
            .mapNotNull { it.repository }
            .map { Git(appHomeDir, it.workspaceName, it.repositoryName) }

        log.info { "sync second tiers, size := ${gitsOfTier2.size}" }
        gitsOfTier2.forEach { git ->
            log.info { "sync: push to (${git.workspaceName}/${git.repositoryName})" }
            git.push(Git.DEFAULT_BRANCH)
        }

        return RepeatStatus.FINISHED
    }
}