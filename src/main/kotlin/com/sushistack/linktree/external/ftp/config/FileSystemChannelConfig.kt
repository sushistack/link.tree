package com.sushistack.linktree.external.ftp.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.file.dsl.Files
import org.springframework.messaging.MessageHeaders
import java.io.File
import java.nio.file.Paths

@Configuration
@Profile("default", "dev")
class FileSystemChannelConfig(private val appHomeDir: String) {
    companion object {
        private const val REMOTE_DIR_REGEX = "headers['remoteDir']"
        private const val FILE_NAME_REGEX = "headers['fileName']"
    }

    private val baseDir by lazy { "$appHomeDir/ftp" }
    private val log = KotlinLogging.logger {}

    @Bean
    fun fileUploadFlow() =
        IntegrationFlow.from("uploadChannel")
            .enrichHeaders { h -> h.headerExpression("remoteDir", REMOTE_DIR_REGEX) }
            .handle(
                Files.outboundAdapter<Any?> { message ->
                    val remoteDir = message.headers["remoteDir"] as String
                    val uploadPath = Paths.get(baseDir, remoteDir).toString()
                    log.info { "uploadPath := [$uploadPath]" }
                    File(uploadPath)
                }
                    .fileNameExpression(FILE_NAME_REGEX)
                    .autoCreateDirectory(true)
            )
            .get()

    @Bean
    fun uploadChannel() = DirectChannel()

    @Bean
    fun fileDeleteFlow() =
        IntegrationFlow.from("deleteChannel")
            .enrichHeaders { h ->
                h.headerExpression("remoteDir", REMOTE_DIR_REGEX)
                h.headerExpression("fileName", FILE_NAME_REGEX)
            }
            .handle { _: Any, headers: MessageHeaders ->
                val remoteDir = headers["remoteDir"] as String
                val fileName = headers["fileName"] as String
                val fileToDelete = Paths.get(baseDir, remoteDir, fileName).toFile()
                if (fileToDelete.exists()) {
                    val deleted = fileToDelete.delete()
                    if (deleted) {
                        log.info { "Deleted file($fileName) from local directory ($remoteDir)." }
                    } else {
                        log.warn { "Failed to delete file($fileName) from local directory ($remoteDir)." }
                    }
                } else {
                    log.warn { "File($fileName) does not exist in local directory ($remoteDir)." }
                }
                null
            }
            .get()

    @Bean
    fun deleteChannel() = DirectChannel()

    @Bean
    fun fileCheckFlow() =
        IntegrationFlow.from("checkInboundChannel")
            .enrichHeaders { h ->
                h.headerExpression("remoteDir", REMOTE_DIR_REGEX)
                h.headerExpression("fileName", FILE_NAME_REGEX)
            }
            .handle { remoteDir: String, _: MessageHeaders ->
                val checkDir = Paths.get(baseDir, remoteDir).toFile()
                if (checkDir.exists() && checkDir.isDirectory) {
                    checkDir.list()?.toList() ?: emptyList()
                } else {
                    log.warn { "Directory ($remoteDir) does not exist." }
                    emptyList<String>()
                }
            }
            .channel("checkOutboundChannel")
            .get()

    @Bean
    fun checkInboundChannel() = DirectChannel()

    @Bean
    fun checkOutboundChannel() = DirectChannel()
}