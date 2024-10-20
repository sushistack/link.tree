package com.sushistack.linktree.entity.git

import com.sushistack.linktree.entity.BaseTimeEntity
import com.sushistack.linktree.entity.publisher.StaticWebpage
import jakarta.persistence.*

@Entity
@Table(name = "lt_git_repository")
class GitRepository (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repository_seq", nullable = false)
    val repositorySeq: Long = 0,

    @Column(name = "workspace_name", nullable = false)
    val workspaceName: String = "",

    @Column(name = "repository_name", nullable = false)
    val repositoryName: String = "",

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webpage_seq")
    val webpage: StaticWebpage = StaticWebpage(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "git_account_seq")
    val gitAccount: GitAccount = GitAccount()
): BaseTimeEntity()