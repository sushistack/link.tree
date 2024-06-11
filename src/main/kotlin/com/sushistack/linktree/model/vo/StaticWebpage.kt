package com.sushistack.linktree.model.vo

import kotlinx.serialization.Serializable

@Serializable
data class StaticWebpage (
    val domain: String,
    val providerType: String,
    val usedCount: Int,
    var gitRepository: GitRepository? = null
)
