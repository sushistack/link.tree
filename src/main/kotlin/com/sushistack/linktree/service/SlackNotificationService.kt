package com.sushistack.linktree.service

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class SlackNotificationService(
    private val methodsClient: MethodsClient,
    private val slackChannel: String
) {
    private val log = KotlinLogging.logger {}

    fun send(channel: String = slackChannel, message: String) =
        try {
            val res = ChatPostMessageRequest.builder()
                .channel(channel)
                .text(message)
                .build()
                .let { methodsClient.chatPostMessage(it) }
            when(res.isOk) {
                true -> Unit
                false -> { log.error { "error := [${res.error}]" } }
            }
        } catch (e: Exception) {
            log.error(e) { "Error while sending message." }
        }
}