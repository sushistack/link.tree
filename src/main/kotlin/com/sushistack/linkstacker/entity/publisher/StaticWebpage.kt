package com.sushistack.linkstacker.model.publisher

import com.sushistack.linkstacker.model.git.GitRepository
import jakarta.persistence.*

@Entity
@Table(name = "ls_static_webpage")
class StaticWebpage (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "webpage_seq", nullable = false)
    val webpageSeq: Long = 0,
    val domain: String = "",
    val providerType: ServiceProviderType = ServiceProviderType.UNKNOWN,
    val repository: GitRepository
)