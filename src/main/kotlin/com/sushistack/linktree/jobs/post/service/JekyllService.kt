package com.sushistack.linktree.jobs.post.service

import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.entity.publisher.StaticWebpage
import com.sushistack.linktree.utils.git.Git
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@Service
class JekyllService(private val appHomeDir: String) {
    companion object {
        private const val DEFAULT_BRANCH = "gh-pages"
        private const val DEPLOY_BRANCH = "master"
        private val timeout: (ServiceProviderType) -> Long = { providerType ->
            when(providerType) {
                ServiceProviderType.PRIVATE_BLOG_NETWORK -> 120L
                ServiceProviderType.CLOUD_BLOG_NETWORK -> 240L
                else -> 10L
            }
        }
    }

    private val log = KotlinLogging.logger {}

    @Retryable(value = [TimeoutException::class], maxAttempts = 3, backoff = Backoff(delay = 2000, multiplier = 2.0))
    fun build(webpage: StaticWebpage) {
        val git = Git(appHomeDir, webpage.repository!!.workspaceName, webpage.repository!!.repositoryName)
        log.info { "[Build Started] ${git.workspaceName}/${git.repositoryName}(${webpage.domain})" }
        git.checkout(DEFAULT_BRANCH)
        git.pull(branch = DEFAULT_BRANCH)

        if (git.branchExists(DEPLOY_BRANCH)) {
            git.deleteBranch(DEPLOY_BRANCH)
        }
        if (!git.branchExists(DEPLOY_BRANCH)) {
            git.createBranch(DEPLOY_BRANCH)
        }

        git.checkout(DEPLOY_BRANCH)

        val outputLines = mutableListOf<String>()
        try {
            val process = ProcessBuilder(listOf("bash", "-c", "bundle update nokogiri ffi; bundle install; JEKYLL_ENV=production bundle exec jekyll build;"))
                .directory(File(appHomeDir, "repo/${git.workspaceName}/${git.repositoryName}"))
                .redirectErrorStream(true)
                .start()

            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    outputLines.add(line ?: "")
                }
            }

            val timeoutSeconds = timeout(webpage.providerType)
            val processedInTime = process.waitFor(timeoutSeconds, TimeUnit.SECONDS)
            log.info { "${git.workspaceName}/${git.repositoryName}(${webpage.domain}) build processing..." }

            if (!processedInTime) {
                process.destroy()
                throw TimeoutException("process timed out after $timeoutSeconds")
            }

            require(process.exitValue() == 0) { "Process exited with code ${process.exitValue()}" }
        } catch (ex: TimeoutException) {
            log.error(ex) { "Error occurred while building Jekyll with Timeout, ${git.workspaceName}/${git.repositoryName}(${webpage.domain})" }
            log.info { outputLines.joinToString("\n") }
            throw ex
        } catch (ex: Exception) {
            log.error(ex) { "Error occurred while building Jekyll." }
        } finally {
            log.info { "[Build Ended] ${git.workspaceName}/${git.repositoryName}(${webpage.domain})" }
        }
    }

}