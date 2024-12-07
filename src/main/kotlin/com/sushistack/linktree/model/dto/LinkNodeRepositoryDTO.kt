package com.sushistack.linktree.model.dto

data class LinkNodeRepositoryDTO(
    val nodeSeq: Long,
    val tier: Int,
    val repositorySeq: Long,
    val workspaceName: String,
    val repositoryName: String,
    val domain: String,
    val uri: String,
    val filePath: String,
    val accountSeq: Long,
    val username: String,
    val appPassword: String
) {
    val url: String by lazy { "https://$domain/$uri" }
}
