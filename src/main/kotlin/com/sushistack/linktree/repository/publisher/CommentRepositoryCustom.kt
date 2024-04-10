package com.sushistack.linktree.repository.publisher

import com.sushistack.linktree.entity.publisher.Comment

interface CommentRepositoryCustom {
    fun findByOrderByUsedCountLimit(limit: Long): List<Comment>
}