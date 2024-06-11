package com.sushistack.linktree.service

import com.sushistack.linktree.entity.publisher.CommentableWebpage
import com.sushistack.linktree.repository.publisher.CommentableWebpageRepository
import org.springframework.stereotype.Service

@Service
class CommentableWebpageService(private val commentableWebpageRepository: CommentableWebpageRepository) {

    fun findByOrderByUsedCountLimit(limit: Long): List<CommentableWebpage> =
        commentableWebpageRepository.findByOrderByUsedCountLimit(limit)

}