package com.sushistack.linkstacker.model.git

import jakarta.persistence.*

@Entity
@Table(name = "ls_git_account")
class GitAccount(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "git_account_seq", nullable = false)
    val accountSeq: Long = 0,

    @Column(name = "username", nullable = false)
    val username: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "hosting_service", nullable = false)
    val hostingService: HostingService = HostingService.UNKNOWN
) {
    companion object {
        fun empty() = GitAccount(0, "", HostingService.UNKNOWN)
    }
}