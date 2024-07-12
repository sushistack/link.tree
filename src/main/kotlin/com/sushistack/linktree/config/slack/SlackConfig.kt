package com.sushistack.linktree.config.slack

import com.slack.api.Slack
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "slack")
class SlackConfig {
    lateinit var botToken: String
    lateinit var channel: String

    @Bean
    fun slack() = Slack.getInstance()!!

    @Bean
    fun methodsClient(slack: Slack) = slack.methods(botToken)!!

    @Bean(name = ["slackChannel"])
    fun slackChannel() = channel
}