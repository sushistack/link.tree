package com.sushistack.linktree.external.ftp

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.nio.file.Files
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

    suspend fun upload(workspaceName: String, repositoryName: String, domain: String) {
        val repoDir = Paths.get("$appHomeDir/repo/${workspaceName}/${repositoryName}")
        log.debug { "repoDir $repoDir" }
        require(repoDir.isDirectory()) { "$repoDir is not a directory" }

        val files = repoDir.toFile().listFiles() ?: emptyArray()
        val remoteDir = "$FTP_REMOTE_DIR/$domain/life"
        val remoteFiles = getFilesOnRemote(remoteDir)

        files.forEach { file ->
            if (remoteFiles.contains(file.name)) {
                ftpGateway.uploadFile(remoteDir, "", Files.readAllBytes(file.toPath()))
                log.info { "Uploaded file(${file.name}) to remote server." }
            } else {
                log.info { "$remoteDir/${file.name} is already exists." }
            }
        }
    }

    suspend fun getFilesOnRemote(remoteDir: String): List<String> =
        ftpGateway.getFiles(remoteDir)
}