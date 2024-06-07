package com.sushistack.linktree.external.git

import io.github.oshai.kotlinlogging.KotlinLogging
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File

class GitRepositoryUtil {
    companion object {

        private val log = KotlinLogging.logger {}

        fun open(
            appHomeDir: String,
            workspaceName: String,
            repositoryName: String,
            appPassword: String
        ): Git {
            val repoPath = File(appHomeDir, "repo/$workspaceName/$repositoryName")
            val gitDir = File(repoPath, ".git")

            return if (gitDir.exists()) {
                val repository = FileRepositoryBuilder()
                    .setGitDir(gitDir)
                    .readEnvironment()
                    .findGitDir()
                    .build()
                Git(repository)
            } else {
                val remoteUrl = "https://bitbucket.org/$workspaceName/$repositoryName.git"
                log.info { "Remote Repository URL: $remoteUrl" }

                try {
                    Git.cloneRepository()
                        .setURI(remoteUrl)
                        .setDirectory(repoPath)
                        .setCredentialsProvider(UsernamePasswordCredentialsProvider(workspaceName, appPassword))
                        .call()
                } catch (e: Exception) {
                    log.info { "Failed to clone remote repository: ${e.message}" }
                    throw IllegalArgumentException("Failed to clone remote repository: ${e.message}", e)
                }
            }
        }
    }
}