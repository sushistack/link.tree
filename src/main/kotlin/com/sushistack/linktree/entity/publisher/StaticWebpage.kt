package com.sushistack.linktree.entity.publisher

import com.sushistack.linktree.entity.BaseTimeEntity
import com.sushistack.linktree.entity.content.Post
import com.sushistack.linktree.entity.git.GitRepository
import jakarta.persistence.*

@Entity
@Table(name = "lt_static_webpage")
class StaticWebpage (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "webpage_seq", nullable = false)
    val webpageSeq: Long = 0,

    @Column(name = "domain", nullable = false)
    val domain: String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false)
    val providerType: ServiceProviderType = ServiceProviderType.UNKNOWN,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "git_repo_seq", nullable = false)
    val repository: GitRepository = GitRepository(),

    @OneToMany(mappedBy = "webpage", fetch = FetchType.LAZY)
    val posts: List<Post> = emptyList()
): BaseTimeEntity()