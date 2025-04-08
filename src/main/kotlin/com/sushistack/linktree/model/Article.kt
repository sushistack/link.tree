package com.sushistack.linktree.model

import kotlinx.serialization.Serializable

@Serializable
data class Article (
    val title: String,
    val description: String,
    var content: String
) {
    val wordCount: Int by lazy { content.split(" ").size }

    fun getSafeTitle() = title
        .replace("<", "")
        .replace(">", "")
        .replace("\"", "")

}