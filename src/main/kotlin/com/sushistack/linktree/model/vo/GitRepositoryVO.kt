package com.sushistack.linktree.model.vo

import com.sushistack.linktree.entity.git.GitAccount
import com.sushistack.linktree.entity.git.GitRepository
import kotlinx.serialization.Serializable

@Serializable
data class GitRepositoryVO(
    val account: String,
    val workspaceName: String,
    val repositoryName: String,
) {
    fun toEntity(gitAccount: GitAccount, decrypt: (String) -> String) = GitRepository(
        workspaceName = decrypt(workspaceName),
        repositoryName = decrypt(repositoryName),
        gitAccount = gitAccount
    )
}