package com.sushistack.linktree.external.ftp

import com.sushistack.linktree.utils.ellipsis
import com.sushistack.linktree.utils.git.*
import com.sushistack.linktree.utils.git.enums.ResetType
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.isDirectory

@Service
class FTPService(private val ftpGateway: FTPGateway) {
    companion object {
        private const val FTP_REMOTE_DIR = "/public_html"
        private const val DEFAULT_BRANCH = "gh-pages"
        private const val DEPLOY_BRANCH = "master"
    }

    private val log = KotlinLogging.logger {}

    @Retryable(value = [Exception::class], maxAttempts = 3, backoff = Backoff(delay = 2000L, multiplier = 2.0))
    fun upload(git: Git, domain: String) {
        val repoDir = git.repoDir
        val targetDir = Paths.get(repoDir, "life")
        require(targetDir.isDirectory()) { "$targetDir is not a directory" }

        val files = targetDir.toFile().listFiles() ?: emptyArray()
        val remoteDir = "$FTP_REMOTE_DIR/$domain/life"
        var remoteFiles = emptyList<String>()
        try {
            remoteFiles = ftpGateway.getFiles(remoteDir)
        } catch (e: Exception) {
            log.error(e) { "failed to fetch files := [${e.message}], remote dir := [$remoteDir]" }
        }

        log.info { "files: ${ellipsis(files.map { it.name })}" }
        log.info { "remote: ${ellipsis(remoteFiles)}" }

        val filesToUpload: List<File> = files.filter { f -> remoteFiles.none { rf -> rf == f.name } }.filter { it.isFile }
        val filesToDelete = remoteFiles.subtract(files.map { it.name }.toSet()).toList()

        log.info { "filesToUpload: ${filesToUpload.map { it.name }}" }
        log.info { "filesToDelete: $filesToDelete" }

        filesToUpload.forEach { file ->
            ftpGateway.uploadFile(remoteDir, file.name, file.readBytes())
            log.info { "Uploaded file(${file.name}) to remote server." }
        }

        filesToDelete.forEach { fileName ->
            ftpGateway.deleteFile(remoteDir, fileName)
            log.info { "Deleted file($fileName) on remote server." }
        }

        git.clean()
        git.reset(type = ResetType.HARD, hash = "HEAD")
        git.checkout(DEFAULT_BRANCH)
        if (git.branchExists(DEPLOY_BRANCH)) {
            git.deleteBranch(DEPLOY_BRANCH)
        }
    }

}