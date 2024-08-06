package com.sushistack.linktree.jobs.post.service

import com.sushistack.linktree.config.measure.MeasureTime
import com.sushistack.linktree.utils.git.Git
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

@Service
class JekyllService(private val appHomeDir: String) {
    companion object {
        private const val DEFAULT_BRANCH = "gh-pages"
        private const val DEPLOY_BRANCH = "master"
    }

    private val log = KotlinLogging.logger {}

    @MeasureTime
    fun build(git: Git) {
        log.info { "\nBuild Jekyll for ${git.workspaceName}/${git.repositoryName}\n" }
        git.checkout(DEFAULT_BRANCH)
        git.pull(branch = DEFAULT_BRANCH)

        if (git.branchExists(DEPLOY_BRANCH)) {
            git.deleteBranch(DEPLOY_BRANCH)
        }
        if (!git.branchExists(DEPLOY_BRANCH)) {
            git.createBranch(DEPLOY_BRANCH)
        }

        git.checkout(DEPLOY_BRANCH)

        try {
            val process = ProcessBuilder(listOf("bash", "-c", "bundle update nokogiri ffi; bundle install; JEKYLL_ENV=production bundle exec jekyll build;"))
                .directory(File(appHomeDir, "repo/${git.workspaceName}/${git.repositoryName}"))
                .redirectErrorStream(true)
                .start()

            BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    log.info { line }
                }
            }

            val exitCode = process.waitFor()
            require(exitCode == 0) { "Process exited with code $exitCode" }
        } catch (ex: Exception) {
            log.error(ex) { "Error occurred while building Jekyll" }
            throw ex
        }
    }

}