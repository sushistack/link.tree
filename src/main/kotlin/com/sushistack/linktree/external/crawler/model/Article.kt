package com.sushistack.linktree.external.crawler.model

import kotlinx.serialization.Serializable

@Serializable
data class Article (
    val title: String,
    val description: String,
    var content: String
) {
    fun getSafeTitle() = title
        .replace("<", "")
        .replace(">", "")
        .replace("\"", "")
}