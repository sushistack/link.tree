package com.sushistack.linktree.repository.content

import com.sushistack.linktree.entity.content.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long>