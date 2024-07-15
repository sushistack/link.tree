package com.sushistack.linktree.jobs.post.service

import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.entity.publisher.ServiceProviderType.CLOUD_BLOG_NETWORK
import com.sushistack.linktree.entity.publisher.ServiceProviderType.PRIVATE_BLOG_NETWORK
import com.sushistack.linktree.external.ftp.FTPService
import com.sushistack.linktree.external.git.GitRepositoryUtil
import com.sushistack.linktree.external.git.addAndCommit
import com.sushistack.linktree.external.git.push
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

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

    suspend fun makePackage(repo: SimpleGitRepository) {
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

        val repoPath = "$appHomeDir/repo/${repo.workspaceName}/${repo.repositoryName}"

        withContext(Dispatchers.IO) {
            val removeTargets = Files.walk(Paths.get(repoPath))
                .filter { it != Paths.get(repoPath) }
                .filter { Files.isRegularFile(it) }
                .filter { listOf(DEPLOY_DIR, ".git", ".gitignore").all { f -> !Paths.get(repoPath).relativize(it.toAbsolutePath()).startsWith(f) } }
                .sorted(Comparator.reverseOrder())
                .toList()

            removeTargets.forEach {
                try {
                    withContext(Dispatchers.IO) {
                        Files.delete(it)
                    }
                    log.info { "Deleted := [$it]" }
                } catch (e: IOException) {
                    log.error(e) { "Error deleting file := [$it]" }
                }
            }
        }

        val deployDir = Path.of(repoPath, DEPLOY_DIR)
        val parentDir = deployDir.parent


        withContext(Dispatchers.IO) {
            Files.list(deployDir).toList().forEach {
                try {
                    val targetPath = parentDir.resolve(it.fileName)
                    withContext(Dispatchers.IO) {
                        Files.move(it, targetPath, StandardCopyOption.REPLACE_EXISTING)
                    }
                    log.info { "Moved := [$it -> $targetPath]" }
                } catch (e: IOException) {
                    log.error(e) { "Failed to move file := [$it]" }
                }
            }
        }

        withContext(Dispatchers.IO) {
            Files.deleteIfExists(deployDir)
        }
        log.info { "Deploy directory is deleted := [${deployDir.fileName}]" }

        ls(Paths.get(repoPath))

    }

    private fun ls(dir: Path) {
        Files.list(dir).use {
            it.forEach { path -> log.info { path } }
        }
    }

    suspend fun deploy(serviceProviderType: ServiceProviderType, repo: SimpleGitRepository) {
        when (serviceProviderType) {
            PRIVATE_BLOG_NETWORK -> ftpService.upload(repo.workspaceName, repo.repositoryName, repo.domain)
            CLOUD_BLOG_NETWORK -> uploadToRemoteOrigin(repo)
            else -> Unit
        }

        val git = GitRepositoryUtil.open(appHomeDir, repo.workspaceName, repo.repositoryName, repo.appPassword)
        git.checkout().setName(DEFAULT_BRANCH).call()
    }

    @Retryable(value = [Exception::class], maxAttempts = 3, backoff = Backoff(delay = 2000, multiplier = 2.0))
    suspend fun uploadToRemoteOrigin(repo: SimpleGitRepository) {
        val git = GitRepositoryUtil.open(appHomeDir, repo.workspaceName, repo.repositoryName, repo.appPassword)
        git.checkout().setName(DEPLOY_BRANCH).call()

        withContext(Dispatchers.IO) {
            git.addAndCommit(commitMessage = "deploy by system")
            git.push(branchName = DEPLOY_BRANCH, username = repo.username, appPassword = repo.appPassword, force = true)
        }
    }
}