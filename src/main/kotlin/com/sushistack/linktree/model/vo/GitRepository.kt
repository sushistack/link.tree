package com.sushistack.linktree.model.vo

import kotlinx.serialization.Serializable

@Serializable
data class GitRepository(
    val workspaceName: String,
    val repositoryName: String,
    var webpage: StaticWebpage? = null,
    var gitAccount: GitAccount? = null
)