package com.sushistack.linkstacker.repository.git

import com.sushistack.linkstacker.entity.git.GitAccount
import org.springframework.data.jpa.repository.JpaRepository

interface GitAccountRepository : JpaRepository<GitAccount, Long>