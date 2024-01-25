package com.sushistack.linkstacker.model.git

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("git_account")
class GitAccount(
    @Id val id: String? = null,
    val username: String,
    val hostingService: HostingService
)