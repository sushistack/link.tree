package com.sushistack.linktree.jobs.post.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

@Service
class JekyllService(private val appHomeDir: String) {
    private val log = KotlinLogging.logger {}

    suspend fun build(workspaceName: String, repositoryName: String) {

        try {
            val process = withContext(Dispatchers.IO) {
                ProcessBuilder(listOf("bash", "-c", "bundle install; bundle update; JEKYLL_ENV=production bundle exec jekyll build;"))
                    .directory(File(appHomeDir, "repo/$workspaceName/$repositoryName"))
                    .redirectErrorStream(true)
                    .start()
            }

            withContext(Dispatchers.IO) {
                BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        log.info { line }
                    }
                }
            }

            val exitCode = withContext(Dispatchers.IO) {
                process.waitFor()
            }
            require(exitCode == 0) { "Process exited with code $exitCode" }
        } catch (ex: Exception) {
            log.error(ex) { "Error occurred while building Jekyll" }
            throw ex
        }
    }

}