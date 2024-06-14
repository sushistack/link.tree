package com.sushistack.linktree.model.vo

import com.sushistack.linktree.entity.publisher.CommentableWebpage
import kotlinx.serialization.Serializable

@Serializable
data class CommentableWebpageVO(
    val postUrl: String,
    val usedCount: Int
) {
    fun toEntity(decrypt: (String) -> String) = CommentableWebpage(
        postUrl = decrypt(postUrl),
        usedCount = usedCount
    )
}