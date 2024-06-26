package com.sushistack.linktree.external.ftp

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.integration.support.MessageBuilder
import org.springframework.messaging.MessageChannel
import org.springframework.stereotype.Service
import java.nio.file.Path

@Service
class FTPService(private val ftpOutboundChannel: MessageChannel) {
    private val log = KotlinLogging.logger {}

    suspend fun upload(filePath: Path) {
        log.debug { "File path to upload: $filePath" }
        val file = filePath.toFile()
        require(file.isFile) { "is not file" }

        MessageBuilder.withPayload(file)
            .setHeader("remoteDir", "/remote/outbound")
            .build()
            .let { ftpOutboundChannel.send(it) }

        log.info { "Uploaded file to remote server." }
    }
}