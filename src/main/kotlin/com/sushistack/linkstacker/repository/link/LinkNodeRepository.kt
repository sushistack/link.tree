package com.sushistack.linkstacker.repository.link

import com.sushistack.linkstacker.entity.link.LinkNode
import org.springframework.data.jpa.repository.JpaRepository

interface LinkNodeRepository : JpaRepository<LinkNode, Long>