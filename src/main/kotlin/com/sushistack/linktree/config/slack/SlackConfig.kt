package com.sushistack.linktree.config.slack

import com.slack.api.Slack
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "slack")
class SlackConfig {
    var enabled: Boolean = true
    lateinit var botToken: String
    var channel: Map<String, String> = mutableMapOf()

    @Bean
    fun slack() = Slack.getInstance()!!

    @Bean
    fun methodsClient(slack: Slack) = slack.methods(botToken)!!

    @Bean(name = ["workflowChannel"])
    fun workflowChannel(): String = channel["workflow"] ?: throw IllegalStateException("Channel not set!")

    @Bean(name = ["reportChannel"])
    fun reportChannel(): String = channel["report"] ?: throw IllegalStateException("Channel not set!")

    @Bean(name = ["validationChannel"])
    fun validationChannel(): String = channel["validation"] ?: throw IllegalStateException("Channel not set!")

}