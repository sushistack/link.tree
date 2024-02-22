package com.sushistack.linktree.repository.publisher

import com.sushistack.linktree.entity.publisher.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long>