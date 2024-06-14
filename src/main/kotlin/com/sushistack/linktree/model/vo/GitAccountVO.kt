package com.sushistack.linktree.model.vo

import com.sushistack.linktree.entity.git.GitAccount
import com.sushistack.linktree.entity.git.HostingService
import kotlinx.serialization.Serializable

@Serializable
data class GitAccountVO(
    val username: String,
    val appPassword: String,
    val hostingService: String
) {
    fun toEntity(decrypt: (String) -> String) = GitAccount(
        username = decrypt(username),
        appPassword = decrypt(appPassword),
        hostingService = HostingService.valueOf(hostingService)
    )
}