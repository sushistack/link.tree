package com.sushistack.linktree.repository.publisher

import com.sushistack.linktree.entity.publisher.CommentableWebpage

interface CommentableWebpageRepositoryCustom {
    fun findByOrderByUsedCountLimit(limit: Long): List<CommentableWebpage>
}