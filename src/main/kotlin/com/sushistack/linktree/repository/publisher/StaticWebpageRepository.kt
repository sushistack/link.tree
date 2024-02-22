package com.sushistack.linktree.repository.publisher

import com.sushistack.linktree.entity.publisher.StaticWebpage
import org.springframework.data.jpa.repository.JpaRepository

interface StaticWebpageRepository: JpaRepository<StaticWebpage, Long>