package com.sushistack.linktree.model.vo

import kotlinx.serialization.Serializable

@Serializable
data class GitAccount(
    val username: String,
    val appPassword: String,
    val hostingService: String
)