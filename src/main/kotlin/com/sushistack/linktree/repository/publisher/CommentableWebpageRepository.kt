package com.sushistack.linktree.repository.publisher

import com.sushistack.linktree.entity.publisher.CommentableWebpage
import org.springframework.data.jpa.repository.JpaRepository

interface CommentableWebpageRepository : JpaRepository<CommentableWebpage, Long>, CommentableWebpageRepositoryCustom