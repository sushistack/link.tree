package com.sushistack.linkstacker.entity.git

import com.sushistack.linkstacker.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "ls_git_repository")
class GitRepository (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repository_seq", nullable = false)
    val repositorySeq: Long = 0,

    @Column(name = "repository_name", nullable = false)
    val repositoryName: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    val gitAccount: GitAccount = GitAccount()
): BaseTimeEntity()