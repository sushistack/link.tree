package com.sushistack.linktree.external.git

import io.github.oshai.kotlinlogging.KotlinLogging
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.PullResult
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.transport.PushResult
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider

const val DEFAULT_HEAD_REF = "HEAD"
private const val DEFAULT_RESET_COMMIT_REF = "HEAD~1"
private const val DEFAULT_REMOTE_NAME = "origin"
private const val DEFAULT_BRANCH_NAME = "gh-pages"
private val log = KotlinLogging.logger {}

fun Git.pullChanges(
    remoteName: String = DEFAULT_REMOTE_NAME,
    branchName: String = DEFAULT_BRANCH_NAME,
    username: String,
    appPassword: String
): PullResult? = this.pull()
        .setRemote(remoteName)
        .setRemoteBranchName(branchName)
        .setCredentialsProvider(UsernamePasswordCredentialsProvider(username, appPassword))
        .call()

fun Git.addAndCommit(filePath: String = ".", commitMessage: String = "Add Post"): RevCommit? {
    status(this)

    this.add()
        .addFilepattern(filePath)
        .call()
    log.info { "Staged Files on $filePath" }

    status(this)

    val statusAfter = this.status().call()
    with(statusAfter) {
        if (added.isEmpty() && changed.isEmpty() && removed.isEmpty()) {
            log.info { "No files are staged for commit. Skipping commit." }
            return null
        }
    }

    val commit = this.commit()
        .setMessage(commitMessage)
        .call()
    log.info { "Complete Commit(${commit.id.name})" }

    return commit
}

fun Git.resetTo(commitId: String = DEFAULT_RESET_COMMIT_REF, mode: ResetCommand.ResetType = ResetCommand.ResetType.HARD): Ref {
    val resultRes = this.reset()
        .setMode(mode)
        .setRef(commitId)
        .call()

    log.info { "Complete Reset of $commitId (mode: $mode): $resultRes" }
    return resultRes
}

fun Git.push(remoteName: String = DEFAULT_REMOTE_NAME, branchName: String = DEFAULT_BRANCH_NAME, username: String, appPassword: String, force: Boolean = false): Iterable<PushResult> {
    val pushResult = this.push()
        .setRemote(remoteName)
        .add(branchName)
        .setForce(force)
        .setCredentialsProvider(UsernamePasswordCredentialsProvider(username, appPassword))
        .call()

    log.info { "Complete Push - $remoteName, Branch - $branchName" }
    return pushResult
}

fun status(git: Git) {
    with(git.status().call()) {
        log.info { "Added: $added" }
        log.info { "Changed: $changed" }
        log.info { "Untracked: $untracked" }
        log.info { "Removed: $removed" }
    }

}

fun Git.getCommitId(ref: String = DEFAULT_HEAD_REF): String? =
    this.repository.use { it.resolve(ref)?.name }

fun Git.cleanup(dir: Boolean = true, force: Boolean = true) {
    status(this)

    this.clean()
        .setCleanDirectories(dir)
        .setForce(force)
        .call()

    status(this)
}