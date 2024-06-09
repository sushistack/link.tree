package com.sushistack.linktree.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CommonConfig {

    @Bean(name = ["appHomeDir"])
    fun appHomeDir(@Value("\${spring.application.name}") appName: String): String =
        "${System.getProperty("user.home")}/${appName}"

}