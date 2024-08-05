package com.sushistack.linktree.external.llm.impl

import com.cohere.api.Cohere
import com.cohere.api.requests.ChatRequest
import com.sushistack.linktree.external.llm.LargeLanguageModelService
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service

@Service
class CohereService(
    private val cohereClients: List<Cohere>
): LargeLanguageModelService {

    @Retryable(value = [Exception::class], maxAttempts = 3, backoff = Backoff(delay = 2000L, multiplier = 2.0))
    override fun call(query: String): String {
        val cohereClient = cohereClients.shuffled().take(1)[0]

        val res = cohereClient.chat(
            ChatRequest.builder()
                .message(query)
                .build()
        )

        return res.text
    }
}