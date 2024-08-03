package com.sushistack.linktree.config.transaction

import com.sushistack.linktree.utils.git.Git
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager

@Component
class TransactionCallbackHandler {
    private val log = KotlinLogging.logger {}

    fun registerCallback(git: Git, onCommit: (Git) -> Unit, onRollback: (Git) -> Unit) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
                override fun afterCompletion(status: Int) {
                    when (status) {
                        TransactionSynchronization.STATUS_COMMITTED -> onCommit(git)
                        TransactionSynchronization.STATUS_ROLLED_BACK -> onRollback(git)
                    }
                }
            })
        } else {
            log.info { "No active transaction to register callbacks." }
        }
    }
}