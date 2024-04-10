package com.sushistack.linktree.service

import com.sushistack.linktree.entity.publisher.Comment
import com.sushistack.linktree.repository.publisher.CommentRepository
import org.springframework.stereotype.Service

@Service
class CommentService(private val commentRepository: CommentRepository) {

    fun findByOrderByUsedCountLimit(limit: Long): List<Comment> =
        commentRepository.findByOrderByUsedCountLimit(limit)

}