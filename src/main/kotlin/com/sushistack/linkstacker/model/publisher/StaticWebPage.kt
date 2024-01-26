package com.sushistack.linkstacker.model.publisher

import com.sushistack.linkstacker.model.git.GitRepository
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("static_web_page")
class StaticWebPage (
    @Id val id: String? = null,
    val publisherType: PublisherType,
    val domain: String,
    val providerType: ServiceProviderType,
    val repository: GitRepository
)