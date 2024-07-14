package com.sushistack.linktree.external.ftp.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.channel.ExecutorChannel
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.file.remote.gateway.AbstractRemoteFileOutboundGateway
import org.springframework.integration.ftp.dsl.Ftp
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory
import java.util.concurrent.Executors

@Configuration
class FTPChannelConfig(
    private val ftpSessionFactory: DefaultFtpSessionFactory
) {

    @Bean
    fun ftpUploadFlow() = IntegrationFlow.from("uploadInboundChannel")
        .handle(
            Ftp.outboundAdapter(ftpSessionFactory)
                .remoteDirectoryExpression("headers['remoteDir']")
                .fileNameExpression("headers['fileName']")
        )
        .log("SSSSSSS")
        .get()

    @Bean
    fun uploadInboundChannel() = ExecutorChannel(Executors.newFixedThreadPool(10))

    @Bean
    fun ftpCheckFlow() =
        IntegrationFlow.from("checkInboundChannel")
            .enrichHeaders { h -> h.headerExpression("remoteDir", "headers['remoteDir']") }
            .handle(
                Ftp.outboundGateway(
                    ftpSessionFactory,
                    AbstractRemoteFileOutboundGateway.Command.LS
                ).options(AbstractRemoteFileOutboundGateway.Option.NAME_ONLY)
                    .workingDirExpression("headers['remoteDir']")
            )
            .channel("checkOutboundChannel")
            .get()

    @Bean
    fun checkInboundChannel() = ExecutorChannel(Executors.newFixedThreadPool(10))

    @Bean
    fun checkOutboundChannel() = DirectChannel()
}
