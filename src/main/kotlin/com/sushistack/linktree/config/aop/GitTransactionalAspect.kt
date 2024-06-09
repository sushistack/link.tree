package com.sushistack.linktree.config.aop

import com.sushistack.linktree.config.aop.annotations.GitTransactional
import com.sushistack.linktree.entity.content.Post
import com.sushistack.linktree.external.git.resetTo
import com.sushistack.linktree.service.GitFactory
import io.github.oshai.kotlinlogging.KotlinLogging
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.eclipse.jgit.api.errors.GitAPIException
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronization.STATUS_ROLLED_BACK
import org.springframework.transaction.support.TransactionSynchronizationManager

@Aspect
@Component
class GitTransactionalAspect(
    private val appHomeDir: String,
    private val gitFactory: GitFactory
) {
    private val log = KotlinLogging.logger {}

    @Around("@annotation(gitTransactional) && args(post,..)")
    fun around(
        joinPoint: ProceedingJoinPoint,
        gitTransactional: GitTransactional,
        post: Post
    ): Any? {
        val repo = post.webpage.repository
        val gitAccount = repo.gitAccount

        val git = gitFactory.openRepo(
            appHomeDir = appHomeDir,
            workspaceName = repo.workspaceName,
            repositoryName = repo.repositoryName,
            appPassword = gitAccount.appPassword
        )

        TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
            override fun afterCompletion(status: Int) {
                if (status == STATUS_ROLLED_BACK) {
                    try {
                        git.resetTo()
                        log.info { "Rollback (Reset) Git Repository Because Transaction" }
                    } catch (e: GitAPIException) {
                        log.info { "Failed to reset git Repository: ${e.message}" }
                    }
                }
                git.close()
            }
        })

        return joinPoint.proceed()
    }
}