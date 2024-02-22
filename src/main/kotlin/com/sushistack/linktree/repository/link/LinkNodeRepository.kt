package com.sushistack.linktree.repository.link

import com.sushistack.linktree.entity.link.LinkNode
import org.springframework.data.jpa.repository.JpaRepository

interface LinkNodeRepository : JpaRepository<LinkNode, Long>