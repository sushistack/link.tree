package com.sushistack.linktree.config.ftp

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.file.remote.gateway.AbstractRemoteFileOutboundGateway
import org.springframework.integration.file.remote.gateway.AbstractRemoteFileOutboundGateway.Command
import org.springframework.integration.ftp.dsl.Ftp
import org.springframework.integration.ftp.gateway.FtpOutboundGateway
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory

@Configuration
@Profile("prod")
@ConditionalOnProperty(value = ["spring.batch.job.name"], havingValue = "postDeployJob")
class FTPChannelConfig(
    private val ftpSessionFactory: DefaultFtpSessionFactory
) {
    companion object {
        private const val REMOTE_DIR_REGEX = "headers['remoteDir']"
        private const val FILE_NAME_REGEX = "headers['fileName']"
    }

    @Bean
    fun ftpUploadFlow() = IntegrationFlow.from("uploadChannel")
        .handle(
            Ftp.outboundAdapter(ftpSessionFactory)
                .remoteDirectoryExpression(REMOTE_DIR_REGEX)
                .fileNameExpression(FILE_NAME_REGEX)
        )
        .get()

    @Bean
    fun uploadChannel() = DirectChannel()

    @Bean
    fun ftpDeleteFlow() = IntegrationFlow.from("deleteChannel")
        .enrichHeaders { h ->
            h.headerExpression("remoteDir", REMOTE_DIR_REGEX)
            h.headerExpression("fileName", FILE_NAME_REGEX)
        }
        .handle(
            FtpOutboundGateway(
                ftpSessionFactory,
                Command.RM.command,
                "headers['remoteDir'] + '/' + headers['fileName']"
            )
        ).get()

    @Bean
    fun deleteChannel() = DirectChannel()

    @Bean
    fun ftpCheckFlow() =
        IntegrationFlow.from("checkInboundChannel")
            .enrichHeaders { h -> h.headerExpression("remoteDir", REMOTE_DIR_REGEX) }
            .handle(
                Ftp.outboundGateway(
                    ftpSessionFactory,
                    Command.LS
                ).options(AbstractRemoteFileOutboundGateway.Option.NAME_ONLY)
                    .workingDirExpression(REMOTE_DIR_REGEX)
            )
            .channel("checkOutboundChannel")
            .get()

    @Bean
    fun checkInboundChannel() = DirectChannel()

    @Bean
    fun checkOutboundChannel() = DirectChannel()
}
