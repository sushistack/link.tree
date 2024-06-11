package com.sushistack.linktree.entity.content

enum class PublicationType {
    POST, COMMENT;

    companion object {
        const val POST_DISCRIMINATOR = "POST"
        const val COMMENT_DISCRIMINATOR = "COMMENT"
    }
}