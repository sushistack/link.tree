package com.sushistack.linktree.model

import java.io.Serializable

data class ArticleSource(
    val filePath: String,
    var usedCount: Int = 0
): Serializable {
    fun get(): String {
        usedCount += 1
        return filePath
    }
}

fun List<ArticleSource>.getMinUsed(): ArticleSource? {
    return if (this.isEmpty()) null
    else this.sortedWith(
        compareBy<ArticleSource> { it.usedCount }
            .thenBy { it.filePath }
    ).first()

}