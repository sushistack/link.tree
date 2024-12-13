package com.sushistack.linktree.service

import com.slack.api.methods.MethodsClient
import com.slack.api.methods.SlackApiException
import com.slack.api.methods.SlackFilesUploadV2Exception
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.files.FilesUploadV2Request
import com.slack.api.model.block.Blocks
import com.slack.api.model.block.LayoutBlock
import com.slack.api.model.block.composition.MarkdownTextObject
import com.sushistack.linktree.batch.config.BatchJob.Companion.getDescription
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.BatchStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException

@Service
class SlackNotificationService(
    private val methodsClient: MethodsClient,
    private val slackChannel: String,
    @Value("\${slack.enabled:true}") private val slackEnabled: Boolean
) {
    companion object {
        private val symbol: (Boolean) -> String = { if (it) ":large_green_circle:" else ":red_circle:" }
    }

    private val log = KotlinLogging.logger {}

    fun send(message: String, channel: String = slackChannel) {
        if (!slackEnabled) {
            log.info { "slack is disabled" }
            return
        }
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


    fun send(blocks: List<LayoutBlock>, channel: String = slackChannel) {
        if (!slackEnabled) {
            log.info { "slack is disabled" }
            return
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


    fun sendPostValidations(linkMap1: Map<Int, List<UrlStatus>>) {
        val blocks = mutableListOf<LayoutBlock>()

        blocks.add(Blocks.section { it.text(MarkdownTextObject.builder().text("*Post Validation List(Tier 1)*").build()) })
        blocks.add(Blocks.section { it.fields(linkMap1.map { entry -> MarkdownTextObject.builder().text("*Code(${entry.key} ${symbol(entry.value.all { res -> res.statusCode == 200 })}):* ${entry.value.size}").build() }) })

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

    fun uploadReport(reportFile: File) {
        val request = FilesUploadV2Request.builder()
            .channel(slackChannel)
            .file(reportFile)
            .filename(reportFile.name)
            .title(reportFile.name)
            .initialComment("${reportFile.name} 작업 완료")
            .build()

        try {
            val response = methodsClient.filesUploadV2(request)
            if (response.isOk) {
                log.info { "File uploaded successfully: ${response.file?.permalink}" }
            } else {
                log.error { "Failed to upload file: ${response.error}" }
            }
        } catch (e: IOException) {
            log.error(e) { "Failed to upload file: ${e.message}" }
        } catch (e: SlackApiException) {
            log.error(e) { "Failed to upload file: ${e.message}" }
        } catch (e: SlackFilesUploadV2Exception) {
            log.error(e) { "Failed to upload file: ${e.message}" }
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