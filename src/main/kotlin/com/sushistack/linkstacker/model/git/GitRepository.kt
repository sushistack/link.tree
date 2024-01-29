package com.sushistack.linkstacker.model.git

import jakarta.persistence.*

@Entity
@Table(name = "ls_git_repository")
class GitRepository (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "git_repo_seq", nullable = false)
    val repositorySeq: Long = 0,

    @Column(name = "repo_name", nullable = false)
    val repositoryName: String = "",

    @ManyToOne
    @JoinColumn(name = "account_id")
    val gitAccount: GitAccount = GitAccount.empty()
) {
    companion object {
        fun empty() = GitRepository()
    }
}