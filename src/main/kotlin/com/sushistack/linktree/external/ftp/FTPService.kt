package com.sushistack.linktree.external.ftp

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Paths
import kotlin.io.path.isDirectory

@Service
class FTPService(
    private val appHomeDir: String,
    private val ftpGateway: FTPGateway
) {
    companion object {
        private const val FTP_REMOTE_DIR = "/public_html"
    }

    private val log = KotlinLogging.logger {}

    @Retryable(value = [Exception::class], maxAttempts = 3, backoff = Backoff(delay = 2000, multiplier = 2.0))
    fun upload(workspaceName: String, repositoryName: String, domain: String) {
        val repoDir = Paths.get("$appHomeDir/repo/${workspaceName}/${repositoryName}/life")
        log.debug { "repoDir $repoDir" }
        require(repoDir.isDirectory()) { "$repoDir is not a directory" }

        val files = repoDir.toFile().listFiles() ?: emptyArray()
        val remoteDir = "$FTP_REMOTE_DIR/$domain/life"
        val remoteFiles = ftpGateway.getFiles(remoteDir)

        log.info { "files: ${files.map { it.name }}" }
        log.info { "remote: $remoteFiles" }

        val filesToUpload: List<File> = files.filter { f -> remoteFiles.none { rf -> rf == f.name } }
        val filesToDelete = remoteFiles.subtract(files.map { it.name }.toSet()).toList()

        log.info { "filesToUpload: ${filesToUpload.map { it.name }}" }
        log.info { "filesToDelete: $filesToDelete" }

        filesToUpload.map { file ->
            ftpGateway.uploadFile(remoteDir, file.name, file.readBytes())
            log.info { "Uploaded file(${file.name}) to remote server." }
        }

        filesToDelete.map { fileName ->
            ftpGateway.deleteFile(remoteDir, fileName)
            log.info { "Deleted file($fileName) on remote server." }
        }
    }

}