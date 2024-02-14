package com.sushistack.linkstacker.repository.git

import com.sushistack.linkstacker.entity.git.GitRepository
import org.springframework.data.jpa.repository.JpaRepository

interface GitRepoRepository : JpaRepository<GitRepository, Long>