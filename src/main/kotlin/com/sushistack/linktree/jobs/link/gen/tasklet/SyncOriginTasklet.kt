package com.sushistack.linktree.jobs.link.gen.tasklet

import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.service.LinkNodeService
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
    private val linkNodeService: LinkNodeService
) : Tasklet {
    private val log = KotlinLogging.logger {}

    @Value("#{jobExecutionContext['order']}")
    lateinit var order: Order

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val linksOfTier1 = linkNodeService.findWithPostByOrder(order, tier = 1)
        val linksOfTier2 = linkNodeService.findWithPostByOrder(order, tier = 2)

        log.info { "firstTier links := ${linksOfTier1.size}, secondTier links := ${linksOfTier2.size}" }

        log.info { "sync first tiers" }
        linksOfTier1.forEach { linkNode ->
            val git = Git(appHomeDir, linkNode.workspaceName, linkNode.repositoryName)
            log.info { "sync: push to (${git.workspaceName}/${git.repositoryName})" }
            git.push(Git.DEFAULT_BRANCH)
        }

        log.info { "sync second tiers" }
        linksOfTier2.forEach { linkNode ->
            val git = Git(appHomeDir, linkNode.workspaceName, linkNode.repositoryName)
            log.info { "sync: push to (${git.workspaceName}/${git.repositoryName})" }
            git.push(Git.DEFAULT_BRANCH)
        }

        return RepeatStatus.FINISHED
    }
}