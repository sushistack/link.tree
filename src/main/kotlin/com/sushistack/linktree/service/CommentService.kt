package com.sushistack.linktree.service

import com.sushistack.linktree.entity.content.Comment
import com.sushistack.linktree.repository.content.CommentRepository
import org.springframework.stereotype.Service

@Service
class CommentService(private val commentRepository: CommentRepository) {

    fun createComment(comment: Comment): Comment = commentRepository.save(comment)
}