package com.sushistack.linkstacker.repository.publisher

import com.sushistack.linkstacker.entity.publisher.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long>