package com.sushistack.linktree.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Configuration
class CommonConfig {

    @Bean(name = ["appHomeDir"])
    fun appHomeDir(@Value("\${spring.application.name}") appName: String): String =
        "${System.getProperty("user.home")}/${appName}"

    @Bean
    fun webClient() =
        WebClient.builder()
            .clientConnector(ReactorClientHttpConnector(
                HttpClient
                    .create()
                    .responseTimeout(Duration.ofSeconds(10))
            ))
            .build()

}