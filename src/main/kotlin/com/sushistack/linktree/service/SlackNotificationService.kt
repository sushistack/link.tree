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
import com.sushistack.linktree.model.batch.JobDetail
import com.sushistack.linktree.model.batch.StepDetail
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.batch.core.BatchStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException

@Service
class SlackNotificationService(
    private val methodsClient: MethodsClient,
    private val reportChannel: String,
    private val workflowChannel: String,
    private val validationChannel: String,
    @Value("\${slack.enabled:true}") private val slackEnabled: Boolean,
) {
    companion object {
        private val symbol: (Boolean) -> String = { if (it) ":large_green_circle:" else ":red_circle:" }
    }

    private val log = KotlinLogging.logger {}

    fun send(message: String, channel: String = workflowChannel) {
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


    fun send(blocks: List<LayoutBlock>, channel: String = workflowChannel) {
        if (!slackEnabled) {
            log.info { "slack is disabled" }
            return
        }
        try {
            val res = ChatPostMessageRequest.builder()
                .channel(channel)
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


    fun sendPostValidations(customerName: String, linkMap1: Map<Int, List<UrlStatus>>) {
        val blocks = mutableListOf<LayoutBlock>()
        val isSuccessful = linkMap1[200]?.all { res -> res.statusCode == 200 } ?: false

        blocks.add(Blocks.section { it.text(MarkdownTextObject.builder().text("${symbol(isSuccessful)} Tier1 Posts of $customerName is ${if (isSuccessful) "valid" else "invalid"}!").build()) })
        blocks.add(Blocks.section { it.fields(linkMap1.map { entry -> MarkdownTextObject.builder().text("*Code(${entry.key} ${symbol(entry.value.all { res -> res.statusCode == 200 })}):* ${entry.value.size}").build() }) })

        blocks.add(Blocks.divider())

        linkMap1.flatMap { link -> link.value }
            .forEach { urlStatus ->
                blocks.add(
                    Blocks.section {
                        it.text(MarkdownTextObject.builder()
                            .text("Url: ${urlStatus.url}, Code: (${symbol(urlStatus.statusCode == 200)})${urlStatus.statusCode}")
                            .build()
                        )
                    }
                )
            }

        send(blocks, validationChannel)
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

        send(blocks, workflowChannel)
    }

    fun uploadReport(reportFile: File) {
        try {
            val response = methodsClient.filesUploadV2 { req: FilesUploadV2Request.FilesUploadV2RequestBuilder ->
                req.channel(reportChannel)
                    .file(reportFile)
                    .filename(reportFile.name)
                    .title("${reportFile.name} 작업 완료")
            }
            when(response.isOk) {
                true -> log.info { "✅ 파일 업로드 성공: ${response.file.id}" }
                else -> log.info { "❌ 업로드 실패: ${response.error}" }
            }
        } catch (e: IOException) {
            log.error(e) { "❌ 업로드 실패: ${e.message}" }
        } catch (e: SlackApiException) {
            log.error(e) { "❌ 업로드 실패: ${e.message}" }
        } catch (e: SlackFilesUploadV2Exception) {
            log.error(e) { "❌ 업로드 실패: ${e.message}" }
        }
    }
}
