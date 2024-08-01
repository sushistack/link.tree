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

    @Column(name = "used_count", nullable = false)
    val usedCount: Int = 0,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_seq")
    var repository: GitRepository? = null,

    @OneToMany(mappedBy = "webpage", cascade = [CascadeType.ALL], orphanRemoval = true)
    val posts: List<Post> = emptyList()
): BaseTimeEntity() {
    fun changeRepository(repository: GitRepository?) {
        this.repository = repository
        this.repository?.webpage = this
    }

    fun getPostUrl(post: Post): String =
        "https://${domain}/${post.filePath?.split(".")?.get(0) ?: ""}"

    override fun toString(): String = "StaticWebpage(webpageSeq=$webpageSeq, domain='$domain', providerType=$providerType, usedCount=$usedCount)"
}