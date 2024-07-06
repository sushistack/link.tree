package com.sushistack.linktree.external.ftp.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.ftp.dsl.Ftp
import org.springframework.integration.channel.ExecutorChannel
import org.springframework.integration.file.remote.gateway.AbstractRemoteFileOutboundGateway
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory
import org.springframework.messaging.MessageChannel
import java.util.concurrent.Executors

@Configuration
class FTPChannelConfig(
    private val ftpSessionFactory: DefaultFtpSessionFactory
) {

    @Bean
    fun ftpOutboundFlow() = IntegrationFlow.from("ftpOutboundChannel")
        .handle(
            Ftp.outboundAdapter(ftpSessionFactory)
                .useTemporaryFileName(true)
                .remoteDirectoryExpression("headers['remoteDir']")
        )
        .get()

    @Bean
    fun ftpOutboundChannel(): MessageChannel {
        return ExecutorChannel(Executors.newFixedThreadPool(10))
    }

    @Bean
    fun ftpCheckFlow() = IntegrationFlow.from("ftpInboundChannel")
        .handle(
            Ftp.outboundGateway(
                ftpSessionFactory,
                AbstractRemoteFileOutboundGateway.Command.LS,
                "headers['remoteDir']"
            )
        )
        .get()

    @Bean
    fun ftpInboundChannel(): MessageChannel =
        ExecutorChannel(Executors.newFixedThreadPool(10))
}