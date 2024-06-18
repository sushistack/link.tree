package com.sushistack.linktree.model.vo

import com.sushistack.linktree.entity.git.GitRepository
import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.entity.publisher.StaticWebpage
import kotlinx.serialization.Serializable

@Serializable
data class StaticWebpageVO (
    val domain: String,
    val providerType: String,
    val usedCount: Int
) {
    fun toEntity(repository: GitRepository, decrypt: (String) -> String) = StaticWebpage(
        domain = decrypt(domain),
        providerType = ServiceProviderType.valueOf(providerType),
        usedCount = usedCount
    ).also { it.changeRepository(repository) }
}
