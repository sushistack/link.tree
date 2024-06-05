package com.sushistack.linktree.external.git

import io.github.oshai.kotlinlogging.KotlinLogging
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

private const val DEFAULT_RESET_COMMIT_REF = "HEAD~1"
private const val DEFAULT_REMOTE_NAME = "origin"
private const val DEFAULT_BRANCH_NAME = "gh-pages"
private val log = KotlinLogging.logger {}

fun Git.addAndCommit(filePath: String, commitMessage: String) {
    this.add()
        .addFilepattern(filePath)
        .call()
    log.info { "Staged Files on $filePath" }

    val commit = this.commit()
        .setMessage(commitMessage)
        .call()
    log.info { "Complete Commit(${commit.id.name})" }
}

fun Git.simpleReset(commitId: String = DEFAULT_RESET_COMMIT_REF, mode: ResetCommand.ResetType = ResetCommand.ResetType.HARD) {
    this.reset()
        .setMode(mode)
        .setRef(commitId)
        .call()

    log.info { "Complete Reset of $commitId (mode: $mode)" }
}

fun Git.push(remoteName: String = DEFAULT_REMOTE_NAME, branchName: String = DEFAULT_BRANCH_NAME, username: String, appPassword: String) {
    val credentialsProvider = UsernamePasswordCredentialsProvider(username, appPassword)

    val pushResult = this.push()
        .setRemote(remoteName)
        .add(branchName)
        .setCredentialsProvider(credentialsProvider)
        .call()

    log.info { "Complete Push - $remoteName, Branch - $branchName" }
    log.info { "Result : $pushResult" }
}


fun main() {
}