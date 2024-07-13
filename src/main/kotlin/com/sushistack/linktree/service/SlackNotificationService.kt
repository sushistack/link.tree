package com.sushistack.linktree.service

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.model.block.Blocks
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import com.sushistack.linktree.batch.config.BatchJob.Companion.getDescription
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class SlackNotificationService(
    private val methodsClient: MethodsClient,
    private val slackChannel: String
) {
    private val log = KotlinLogging.logger {}

    fun send(message: String, channel: String = slackChannel) =
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

    fun sendPostValidations(linkMap1: Map<Int, List<UrlStatus>>, linkMap2: Map<Int, List<UrlStatus>>) {
        val blocks = mutableListOf<LayoutBlock>()

        blocks.add(Blocks.section { it.text(MarkdownTextObject.builder().text("*Post Validation List*").build()) })

        blocks.add(Blocks.section { it.fields(linkMap1.map { entry -> MarkdownTextObject.builder().text("*Code(${entry.key}):* ${entry.value.size}").build() }) })

        blocks.add(Blocks.divider())

        blocks.add(Blocks.section { it.text(MarkdownTextObject.builder().text("*Post Validation List*").build()) })
        blocks.add(Blocks.section { it.fields(linkMap1.map { entry -> MarkdownTextObject.builder().text("*Code(${entry.key}):* ${entry.value.size}").build() }) })
    }

    fun sendJobDetail(jobDetail: JobDetail, stepDetails: List<StepDetail>) {
        val blocks = mutableListOf<LayoutBlock>()
        blocks.add(Blocks.section { it.text(MarkdownTextObject.builder().text("${getDescription(jobDetail.name)} 결과").build()) })

        blocks.add(Blocks.divider())

        blocks.add(
            Blocks.section {
                it.fields(
                    listOf(
                        MarkdownTextObject.builder().text("*Job Name:*\n${jobDetail.name}").build(),
                        MarkdownTextObject.builder().text("*Status:*\n${jobDetail.status}").build(),
                        MarkdownTextObject.builder().text("*Start Time:*\n${jobDetail.startTime}").build(),
                        MarkdownTextObject.builder().text("*End Time:*\n${jobDetail.endTime}").build()
                    )
                )
            }
        )

        blocks.add(Blocks.section { it.text(MarkdownTextObject.builder().text("*Step Details:*").build()) })

        stepDetails.forEach { step ->
            blocks.add(
                Blocks.section {
                    it.fields(
                        listOf(
                            MarkdownTextObject.builder().text("*Step Name:*\n${step.name}").build(),
                            MarkdownTextObject.builder().text("*Status:*\n${step.status}").build()
                        )
                    )
                }
            )
        }

        jobDetail.message?.let { message ->
            blocks.add(Blocks.section { it.text(MarkdownTextObject.builder().text(message).build()) })
        }

        try {
            val res = ChatPostMessageRequest.builder()
                .channel(slackChannel)
                .blocks(blocks)
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
}

data class JobDetail(
    val name: String,
    val status: String,
    val startTime: String,
    val endTime: String,
    val message: String? = null
)

data class StepDetail(
    val name: String,
    val status: String,
    val startTime: String,
    val endTime: String
)