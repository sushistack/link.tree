package com.sushistack.linktree.entity.git

import com.sushistack.linktree.entity.BaseTimeEntity
import com.sushistack.linktree.model.common.enums.AuthorizationScheme
import jakarta.persistence.*
import java.util.Base64

@Entity
@Table(name = "ls_git_account")
class GitAccount(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "git_account_seq", nullable = false)
    val accountSeq: Long = 0,

    @Column(name = "username", nullable = false)
    val username: String = "",

    @Column(name = "appPassword", nullable = false)
    val appPassword: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "hosting_service", nullable = false)
    val hostingService: HostingService = HostingService.UNKNOWN,

    @OneToMany(mappedBy = "gitAccount")
    val gitRepositories: List<GitRepository> = mutableListOf()
): BaseTimeEntity() {

    fun getAuthorization(authScheme: AuthorizationScheme = AuthorizationScheme.BASIC): String =
        "${authScheme.prefix} " +
        Base64.getEncoder()
            .encodeToString("${this.username}:${this.appPassword}".toByteArray())
}