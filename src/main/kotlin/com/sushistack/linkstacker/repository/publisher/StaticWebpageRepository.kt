package com.sushistack.linkstacker.repository.publisher

import com.sushistack.linkstacker.entity.publisher.StaticWebpage
import org.springframework.data.jpa.repository.JpaRepository

interface StaticWebpageRepository: JpaRepository<StaticWebpage, Long>