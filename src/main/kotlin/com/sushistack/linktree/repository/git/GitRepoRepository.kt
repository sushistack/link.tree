package com.sushistack.linktree.repository.git

import com.sushistack.linktree.entity.git.GitRepository
import org.springframework.data.jpa.repository.JpaRepository

interface GitRepoRepository : JpaRepository<GitRepository, Long>