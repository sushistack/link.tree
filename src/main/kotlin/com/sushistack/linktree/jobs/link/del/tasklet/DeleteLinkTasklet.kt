package com.sushistack.linktree.jobs.link.del.tasklet

import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.order.OrderStatus
import com.sushistack.linktree.model.dto.LinkNodeRepositoryDTO
import com.sushistack.linktree.service.LinkNodeService
import com.sushistack.linktree.service.OrderService
import com.sushistack.linktree.utils.git.Git
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.isRegularFile

@JobScope
@Component
class DeleteLinkTasklet(
    private val appHomeDir: String,
    private val orderService: OrderService,
    private val linkNodeService: LinkNodeService
): Tasklet {
    private val log = KotlinLogging.logger {}

    @Value("#{jobExecutionContext['order']}")
    private lateinit var  order: Order

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? = runBlocking {
        val tier1Nodes = linkNodeService.findWithPostByOrder(order, tier = 1)

        log.info { "Delete tier1 Links (${tier1Nodes.size})" }
        tier1Nodes.forEach { delete(it) }

        val tier2Nodes = linkNodeService.findWithPostByOrder(order, tier = 2)
        log.info { "Delete tier2 Links (${tier2Nodes.size})" }
        tier2Nodes.forEach { delete(it) }

        if (order.isValid()) {
            order.orderStatus = OrderStatus.DELETED
            orderService.updateOrder(order)
        }
        RepeatStatus.FINISHED
    }

    private fun delete(linkNode: LinkNodeRepositoryDTO) {
        log.info { "Attempt to delete ${linkNode.filePath} of (${linkNode.workspaceName}/${linkNode.repositoryName})" }
        val git = Git(appHomeDir, linkNode.workspaceName, linkNode.repositoryName)
        val file = Paths.get("${git.repoDir}/${linkNode.filePath}")
        if (file.isRegularFile()) {
            Files.delete(file)
            log.info { "$file is deleted." }
            git.addAll()
            git.commit("Delete $file")
        } else {
            log.info { "$file is not exists." }
        }
    }
}
