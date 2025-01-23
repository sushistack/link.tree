package com.sushistack.linktree.jobs.post.service

import com.sushistack.linktree.entity.publisher.ServiceProviderType.CLOUD_BLOG_NETWORK
import com.sushistack.linktree.entity.publisher.ServiceProviderType.PRIVATE_BLOG_NETWORK
import com.sushistack.linktree.entity.publisher.StaticWebpage
import com.sushistack.linktree.service.ftp.FTPService
import com.sushistack.linktree.utils.git.Git
import com.sushistack.linktree.utils.moveRecursivelyTo
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import java.io.IOException
import java.nio.file.Files
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

    fun makePackage(webpage: StaticWebpage) {
        val git = Git(appHomeDir, webpage.repository!!.workspaceName, webpage.repository!!.repositoryName)
        log.info { "[Arrange Directory Started] ${git.workspaceName}/${git.repositoryName}(${webpage.domain})" }
        val repoPath = Paths.get(git.repoDir)
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
            } catch (e: IOException) {
                log.error(e) { "Error deleting file := [${it.name}]" }
            }
        }

        val deployDir = repoPath.resolve(DEPLOY_DIR)
        val parentDir = deployDir.parent

        try {
            deployDir.toFile().moveRecursivelyTo(parentDir.toFile())
        } catch (e: IOException) {
            log.error(e) { "Failed to move to[$parentDir] from[$deployDir]" }
        }

        Files.deleteIfExists(deployDir)
        log.info { "[Arrange Directory Ended] ${git.workspaceName}/${git.repositoryName}(${webpage.domain})" }
    }

    fun deploy(webpage: StaticWebpage) {
        val git = Git(appHomeDir, webpage.repository!!.workspaceName, webpage.repository!!.repositoryName)
        log.info { "[Deploy Started] ${git.workspaceName}/${git.repositoryName}(${webpage.domain})" }
        when (webpage.providerType) {
            PRIVATE_BLOG_NETWORK -> ftpService.upload(git, webpage.domain)
            CLOUD_BLOG_NETWORK -> uploadToRemoteOrigin(git)
            else -> Unit
        }
        log.info { "[Deploy Ended] ${git.workspaceName}/${git.repositoryName}(${webpage.domain})" }
    }

    @Retryable(value = [Exception::class], maxAttempts = 3, backoff = Backoff(delay = 2000, multiplier = 2.0))
    fun uploadToRemoteOrigin(git: Git) {
        git.addAll()
        git.commit("deploy by system")
        git.push(branch = DEPLOY_BRANCH, force = true)

        git.clean()
        git.checkout(DEFAULT_BRANCH)
    }
}