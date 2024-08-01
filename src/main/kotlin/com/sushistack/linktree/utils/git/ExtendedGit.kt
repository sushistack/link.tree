package com.sushistack.linktree.utils.git

import com.sushistack.linktree.external.git.*
import io.github.oshai.kotlinlogging.KotlinLogging
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.lib.Ref

private val log = KotlinLogging.logger {}

data class ExtendedGit(
    val appHomeDir: String,
    val workspaceName: String,
    val repositoryName: String,
    val username: String,
    val appPassword: String
) {
    val git: Git by lazy { GitRepositoryUtil.open(appHomeDir, workspaceName, repositoryName, appPassword) }
    val localRepoPath: String by lazy { "$appHomeDir/repo/${workspaceName}/${repositoryName}" }
}

fun ExtendedGit.pull(
    remoteName: String = DEFAULT_REMOTE_NAME,
    branchName: String = DEFAULT_BRANCH_NAME
) = this.git.pullChanges(remoteName, branchName, username, appPassword)

fun ExtendedGit.addAndCommit(
    filePath: String = ".",
    commitMessage: String = "Add Post"
) = this.git.addAndCommit(filePath, commitMessage)

fun ExtendedGit.reset(
    commitId: String = DEFAULT_RESET_COMMIT_REF,
    mode: ResetCommand.ResetType = ResetCommand.ResetType.HARD
) = this.git.resetTo(commitId, mode)

fun ExtendedGit.push(
    remoteName: String = DEFAULT_REMOTE_NAME,
    branchName: String = DEFAULT_BRANCH_NAME,
    force: Boolean = false
) = this.git.push(remoteName, branchName, username, appPassword, force)

fun ExtendedGit.status() = status(this.git)

fun ExtendedGit.getCommitId(ref: String = DEFAULT_HEAD_REF) = this.git.getCommitId(ref)

fun ExtendedGit.cleanup(
    dir: Boolean = true,
    force: Boolean = true
) = this.git.cleanup(dir, force)

fun ExtendedGit.close() = this.git.close()

fun ExtendedGit.checkout(branchName: String): Ref? {
    return try {
        this.git.checkout()
            .setName(branchName)
            .call()
            .also { log.info { "Checkout to := [${it.name}]" } }
    } catch (e: Exception) {
        log.error(e) { "Can't checkout to := [${e.message}], git($this)" }
        return null
    }
}


fun ExtendedGit.branchCreate(branchName: String) =
    try {
        this.git.branchCreate()
            .setName(branchName)
            .call()
            .also { log.info { "Created Branch := [${it.name}]" } }
    } catch (e: Exception) {
        log.error(e) { "Can't create Branch := [${e.message}], git($this)" }
    }


fun ExtendedGit.branchDelete(branchName: String) {
    try {
        this.git.branchDelete()
            .setBranchNames(branchName)
            .setForce(true)
            .call()
            .also { log.info { "Deleted Branches := [${it.joinToString(",")}]" } }
    } catch (e: Exception) {
        log.error(e) { "Can't delete Branch := [${e.message}], git($this)" }
    }
}
