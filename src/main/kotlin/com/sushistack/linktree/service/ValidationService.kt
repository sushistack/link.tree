package com.sushistack.linktree.service

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class ValidationService(private val webClient: WebClient) {

    fun validatePosts(urls: List<String>) = mono {
        urls.map { url ->
            async {
                try {
                    webClient.get()
                        .uri(url)
                        .exchangeToMono { res -> Mono.just(res.statusCode().value()) }
                        .awaitSingle()
                        .let { UrlStatus(url, it) }
                } catch (e: Exception) {
                    UrlStatus(url, -1, e.message)
                }
            }
        }.awaitAll()
    }

}

data class UrlStatus(
    val url: String,
    val statusCode: Int,
    val error: String? = null
)