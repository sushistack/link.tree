package com.sushistack.linktree.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.*

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
class AuditingConfig {
    @Bean
    fun auditorProvider(): AuditorAware<String> {
        return AuditorAware { Optional.ofNullable("SYSTEM.LINK.TREE") }
    }
}