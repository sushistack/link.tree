package com.sushistack.linktree.external.ftp

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.integration.core.MessagingTemplate
import org.springframework.integration.support.MessageBuilder
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Service
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.isDirectory

@Service
class FTPService(
    private val appHomeDir: String,
    private val ftpInboundChannel: MessageChannel,
    private val ftpOutboundChannel: MessageChannel
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
                MessageBuilder.withPayload(file)
                    .setHeader("remoteDir", remoteDir)
                    .build()
                    .let { ftpOutboundChannel.send(it) }
                log.info { "Uploaded file(${file.name}) to remote server." }
            } else {
                log.info { "$remoteDir/${file.name} is already exists." }
            }
        }
    }

    fun getFilesOnRemote(remoteDir: String): List<String> {
        val message = MessageBuilder.withPayload("")
            .setHeader("remoteDir", remoteDir)
            .build()

        val res = MessagingTemplate(ftpInboundChannel).sendAndReceive(message)

        return res?.payload as? List<String> ?: emptyList()
    }
}