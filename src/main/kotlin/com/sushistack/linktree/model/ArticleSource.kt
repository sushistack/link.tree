package com.sushistack.linktree.model

data class ArticleSource(
    val filePath: String,
    var usedCount: Int = 0
) {
    fun get(): String {
        usedCount += 1
        return filePath
    }
}

fun List<ArticleSource>.getMinUsed(): ArticleSource? {
    if (this.isEmpty()) return null
    this.sortedBy { it.usedCount }
    return this.first()
}