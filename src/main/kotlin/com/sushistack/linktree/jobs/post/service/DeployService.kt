package com.sushistack.linktree.jobs.post.service

import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.entity.publisher.ServiceProviderType.CLOUD_BLOG_NETWORK
import com.sushistack.linktree.entity.publisher.ServiceProviderType.PRIVATE_BLOG_NETWORK
import com.sushistack.linktree.external.ftp.FTPService
import com.sushistack.linktree.external.git.*
import com.sushistack.linktree.utils.moveRecursivelyTo
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Service
class DeployService(
    private val appHomeDir: String,
    private val ftpService: FTPService
) {
    private val log = KotlinLogging.logger {}

    companion object {
        const val DEFAULT_BRANCH = "gh-pages"
        const val DEPLOY_BRANCH = "master"
        const val DEPLOY_DIR = "_site"
    }

    data class SimpleGitRepository (
        val workspaceName: String,
        val repositoryName: String,
        val domain: String,
        val username: String,
        val appPassword: String
    )

    fun makePackage(repo: SimpleGitRepository) {
        val git = GitRepositoryUtil.open(appHomeDir, repo.workspaceName, repo.repositoryName, repo.appPassword)
        val r = git.checkout().setName(DEFAULT_BRANCH).call()
        log.info { "checkout to := [${r.name}]" }

        val results = git.branchDelete()
            .setBranchNames(DEPLOY_BRANCH)
            .setForce(true)
            .call()

        log.info { "master branch is Deleted := [$results]" }

        val branch = git.branchCreate()
            .setName(DEPLOY_BRANCH)
            .setForce(true)
            .call()

        log.info { "branch is Created := [${branch.name}]" }

        val ref = git.checkout().setName(DEPLOY_BRANCH).call()

        log.info { "checkout to := [${ref.name}]" }

        val repoPath = Paths.get("$appHomeDir/repo/${repo.workspaceName}/${repo.repositoryName}")
        log.info { "before delete ls" }
        ls(repoPath)
        val removeTargets = Files.walk(repoPath)
            .filter { it != repoPath }
            .filter { listOf(DEPLOY_DIR, ".git", ".gitignore").all { f -> !repoPath.relativize(it.toAbsolutePath()).startsWith(f) } }
            .map { it.toFile() }
            .toList()

        removeTargets.forEach {
            try {
                when (it.isDirectory) {
                    true -> it.deleteRecursively()
                    false -> Files.deleteIfExists(it.toPath())
                }
                log.info { "Deleted := [${it.name}]" }
            } catch (e: IOException) {
                log.error(e) { "Error deleting file := [${it.name}]" }
            }
        }

        log.info { "after delete ls" }
        ls(repoPath)

        val deployDir = repoPath.resolve(DEPLOY_DIR)
        val parentDir = deployDir.parent

        try {
            deployDir.toFile().moveRecursivelyTo(parentDir.toFile())
            log.info { "Moved := [$deployDir -> $parentDir]" }
        } catch (e: IOException) {
            log.error(e) { "Failed to move to[$parentDir] from[$deployDir]" }
        }

        Files.deleteIfExists(deployDir)
        log.info { "Deploy directory is deleted := [${deployDir.fileName}]" }

        log.info { "after moved ls" }
        ls(repoPath)
        git.close()
    }

    private fun ls(dir: Path) {
        Files.list(dir).use {
            it.forEach { path -> log.info { path } }
        }
    }

    fun deploy(serviceProviderType: ServiceProviderType, repo: SimpleGitRepository) =
        when (serviceProviderType) {
            PRIVATE_BLOG_NETWORK -> ftpService.upload(repo)
            CLOUD_BLOG_NETWORK -> uploadToRemoteOrigin(repo)
            else -> Unit
        }

    @Retryable(value = [Exception::class], maxAttempts = 3, backoff = Backoff(delay = 2000, multiplier = 2.0))
    fun uploadToRemoteOrigin(repo: SimpleGitRepository) {
        val repoPath = Paths.get("$appHomeDir/repo/${repo.workspaceName}/${repo.repositoryName}")
        ls(repoPath)
        val git = GitRepositoryUtil.open(appHomeDir, repo.workspaceName, repo.repositoryName, repo.appPassword)
        git.checkout().setName(DEPLOY_BRANCH).call()

        git.addAndCommit(commitMessage = "deploy by system")
        git.push(branchName = DEPLOY_BRANCH, username = repo.username, appPassword = repo.appPassword, force = true)

        git.checkout().setName(DEFAULT_BRANCH).call()
        git.cleanup()
        git.resetTo(commitId = DEFAULT_HEAD_REF)
        git.close()
    }
}