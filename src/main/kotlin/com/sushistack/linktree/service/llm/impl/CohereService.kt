package com.sushistack.linktree.service.llm.impl

import com.cohere.api.Cohere
import com.cohere.api.core.CohereApiError
import com.cohere.api.requests.ChatRequest
import com.sushistack.linktree.service.llm.LargeLanguageModelService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service

@Service
class CohereService(
    private val cohereClients: List<Cohere>
): LargeLanguageModelService {

    private val log = KotlinLogging.logger {}

    @Retryable(value = [Exception::class], maxAttempts = 3, backoff = Backoff(delay = 2000L, multiplier = 2.0))
    override fun call(query: String): String {
        val cohereClient = cohereClients.shuffled().take(1)[0]

        try {
            val res = cohereClient.chat(
                ChatRequest.builder()
                    .message(query)
                    .build()
            )
            return res.text
        } catch (ex: CohereApiError) {
            log.info { "Failed to call `$query`, ${ex.message}" }
            return ""
        }
    }
}