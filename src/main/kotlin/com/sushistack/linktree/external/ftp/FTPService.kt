package com.sushistack.linktree.external.ftp

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
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
    suspend fun upload(workspaceName: String, repositoryName: String, domain: String) {
        val repoDir = Paths.get("$appHomeDir/repo/${workspaceName}/${repositoryName}/life")
        log.debug { "repoDir $repoDir" }
        require(repoDir.isDirectory()) { "$repoDir is not a directory" }

        val files = repoDir.toFile().listFiles() ?: emptyArray()
        val remoteDir = "$FTP_REMOTE_DIR/$domain/life"
        val remoteFiles = getFilesOnRemote(remoteDir)

        val filesToUpload: List<File> = files.filter { f -> remoteFiles.none { rf -> rf == f.name } }
        val filesToDelete = remoteFiles.subtract(files.map { it.name }.toSet()).toList()

        runBlocking {
            val jobs1 = async {
                filesToUpload.map { file ->
                    async(Dispatchers.IO) {
                        ftpGateway.uploadFile(remoteDir, file.name, file.readBytes())
                        log.info { "Uploaded file(${file.name}) to remote server." }
                    }
                }.awaitAll()
            }

            val jobs2 = async {
                filesToDelete.map { fileName ->
                    async(Dispatchers.IO) {
                        ftpGateway.deleteFile(remoteDir, fileName)
                        log.info { "Deleted file($fileName) on remote server." }
                    }
                }.awaitAll()
            }

            jobs1.await()
            jobs2.await()
        }
    }

    suspend fun getFilesOnRemote(remoteDir: String): List<String> =
        ftpGateway.getFiles(remoteDir)

    suspend fun deleteFileOnRemote(remoteDir: String, fileName: String) =
        ftpGateway.deleteFile(remoteDir, fileName)
}