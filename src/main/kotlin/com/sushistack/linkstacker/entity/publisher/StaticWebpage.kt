package com.sushistack.linkstacker.entity.publisher

import com.sushistack.linkstacker.entity.BaseTimeEntity
import com.sushistack.linkstacker.entity.git.GitRepository
import jakarta.persistence.*

@Entity
@Table(name = "ls_static_webpage")
class StaticWebpage (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "webpage_seq", nullable = false)
    val webpageSeq: Long = 0,

    @Column(name = "domain", nullable = false)
    val domain: String = "",

    @Column(name = "provider_type", nullable = false)
    val providerType: ServiceProviderType = ServiceProviderType.UNKNOWN,

    @OneToOne
    @JoinColumn(name = "git_repo_seq", nullable = false)
    val repository: GitRepository = GitRepository()
): BaseTimeEntity()