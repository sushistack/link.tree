package com.sushistack.linktree.repository.content

import com.sushistack.linktree.entity.content.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long>