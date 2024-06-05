package com.sushistack.linktree.external.git

import io.github.oshai.kotlinlogging.KotlinLogging
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File

class GitRepositoryUtil {
    val log = KotlinLogging.logger {}

    fun open(
        workspaceName: String,
        repositoryName: String,
        appPassword: String
    ): Git {
        // Spring 프로젝트 루트 디렉토리 가져오기
        val projectRoot = System.getProperty("user.dir")

        // 로컬 저장소 경로 설정
        val repoPath = File(projectRoot, "repositories/$workspaceName/$repositoryName")
        val gitDir = File(repoPath, ".git")

        return if (gitDir.exists()) {
            // 로컬 저장소가 존재하면 엽니다.
            val repository = FileRepositoryBuilder()
                .setGitDir(gitDir)
                .readEnvironment()
                .findGitDir()
                .build()
            Git(repository)
        } else {
            val remoteUrl = "https://bitbucket.org/$workspaceName/$repositoryName.git"
            log.info { "원격 저장소 URL: $remoteUrl" }

            try {
                Git.cloneRepository()
                    .setURI(remoteUrl)
                    .setDirectory(repoPath)
                    .setCredentialsProvider(UsernamePasswordCredentialsProvider(workspaceName, appPassword))
                    .call()
            } catch (e: Exception) {
                log.info { "원격 저장소 클론에 실패했습니다: ${e.message}" }
                throw IllegalArgumentException("원격 저장소 클론에 실패했습니다: ${e.message}", e)
            }
        }
    }
}