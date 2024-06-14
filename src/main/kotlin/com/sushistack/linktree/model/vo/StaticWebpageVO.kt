package com.sushistack.linktree.model.vo

import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.entity.publisher.StaticWebpage
import kotlinx.serialization.Serializable

@Serializable
data class StaticWebpageVO (
    val domain: String,
    val providerType: String,
    val usedCount: Int
) {
    fun toEntity(decrypt: (String) -> String) = StaticWebpage(
        domain = decrypt(domain),
        providerType = ServiceProviderType.valueOf(providerType),
        usedCount = usedCount
    )
}
