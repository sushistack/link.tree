package com.sushistack.linktree.external.llm.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cohere.api")
data class CohereProperties(
    val keys: List<String>
)