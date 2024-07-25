package com.sushistack.linktree.service

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.model.block.Blocks
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import com.sushistack.linktree.batch.config.BatchJob.Companion.getDescription
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.BatchStatus
import org.springframework.stereotype.Service

@Service
class SlackNotificationService(
    private val methodsClient: MethodsClient,
    private val slackChannel: String
) {
    companion object {
        private val symbol: (Boolean) -> String = { if (it) ":green_circle:" else ":red_circle:" }
    }

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

    fun send(blocks: List<LayoutBlock>, channel: String = slackChannel) =
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

    fun sendPostValidations(linkMap1: Map<Int, List<UrlStatus>>, linkMap2: Map<Int, List<UrlStatus>>) {
        val blocks = mutableListOf<LayoutBlock>()

        blocks.add(Blocks.section { it.text(MarkdownTextObject.builder().text("*Post Validation List(Tier 1)*").build()) })
        blocks.add(Blocks.section { it.fields(linkMap1.map { entry -> MarkdownTextObject.builder().text("*Code(${entry.key} ${symbol(entry.value.isEmpty())}):* ${entry.value.size}").build() }) })

        blocks.add(Blocks.divider())

        blocks.add(Blocks.section { it.text(MarkdownTextObject.builder().text("*Post Validation List(Tier 2)*").build()) })
        blocks.add(Blocks.section { it.fields(linkMap2.map { entry -> MarkdownTextObject.builder().text("$symbol *Code(${entry.key} ${symbol(entry.value.isEmpty())}):* ${entry.value.size}").build() }) })

        send(blocks, slackChannel)
    }

    fun sendJobDetail(jobDetail: JobDetail, stepDetails: List<StepDetail>) {
        val isCompleted = jobDetail.status == BatchStatus.COMPLETED.name

        val blocks = mutableListOf<LayoutBlock>()
        blocks.add(Blocks.section { it.text(MarkdownTextObject.builder().text("${getDescription(jobDetail.name)} 결과").build()) })
        blocks.add(Blocks.divider())

        blocks.add(
            Blocks.section {
                it.fields(
                    listOf(
                        MarkdownTextObject.builder().text("*Job Name:*\n${jobDetail.name}").build(),
                        MarkdownTextObject.builder().text("*Status:*\n${symbol(isCompleted)} ${jobDetail.status}").build(),
                        MarkdownTextObject.builder().text("*Start Time:*\n${jobDetail.startTime}").build(),
                        MarkdownTextObject.builder().text("*End Time:*\n${jobDetail.endTime}").build()
                    )
                )
            }
        )

        if (!isCompleted) {
            blocks.add(Blocks.section { it.text(MarkdownTextObject.builder().text("*Step Details:*").build()) })
            stepDetails.forEach { step ->
                val isStepCompleted = step.status == BatchStatus.COMPLETED.name
                blocks.add(
                    Blocks.section {
                        it.fields(
                            listOf(
                                MarkdownTextObject.builder().text("*Step Name:*\n${step.name}").build(),
                                MarkdownTextObject.builder().text("*Status:*\n${symbol(isStepCompleted)} ${step.status}").build()
                            )
                        )
                    }
                )
            }
        }

        jobDetail.message?.let { message ->
            blocks.add(Blocks.section { it.text(MarkdownTextObject.builder().text(message).build()) })
        }

        send(blocks, slackChannel)
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