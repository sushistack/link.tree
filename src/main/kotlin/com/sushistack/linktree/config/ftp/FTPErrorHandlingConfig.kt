package com.sushistack.linktree.config.ftp

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.dsl.IntegrationFlow
import org.springframework.integration.handler.LoggingHandler
import org.springframework.messaging.MessageHandler

@Configuration
class FTPErrorHandlingConfig {

    @Bean
    fun errorLoggingHandler(): MessageHandler =
        LoggingHandler("ERROR")
            .apply { setLoggerName("FTPIntegrationErrorLogger") }

    @Bean
    fun errorFlow() = IntegrationFlow.from("errorChannel")
        .handle(errorLoggingHandler())
        .get()
}