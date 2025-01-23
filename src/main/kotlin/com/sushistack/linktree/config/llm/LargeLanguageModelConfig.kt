package com.sushistack.linktree.config.llm

import com.cohere.api.Cohere
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(CohereProperties::class)
class LargeLanguageModelConfig {

    @Bean
    fun cohereClients(cohereProperties: CohereProperties) =
        cohereProperties.keys.map {
            Cohere.builder()
                .token(it)
                .clientName("cohereClient")
                .build()
        }
}