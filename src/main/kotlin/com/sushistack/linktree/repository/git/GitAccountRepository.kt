package com.sushistack.linktree.repository.git

import com.sushistack.linktree.entity.git.GitAccount
import org.springframework.data.jpa.repository.JpaRepository

interface GitAccountRepository : JpaRepository<GitAccount, Long>