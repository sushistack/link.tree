package com.sushistack.linkstacker.model.git

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("git_repository")
class GitRepository (
    @Id val id: String? = null,
    val name: String,
    val gitAccountId: String?
)