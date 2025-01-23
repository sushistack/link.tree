package com.sushistack.linktree.config.llm

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cohere.api")
data class CohereProperties(
    val keys: List<String>
)