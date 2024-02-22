package com.sushistack.linktree.entity.link

import com.sushistack.linktree.entity.BaseTimeEntity
import com.sushistack.linktree.entity.content.Post
import com.sushistack.linktree.entity.git.GitRepository
import com.sushistack.linktree.entity.order.Order
import jakarta.persistence.*

@Entity
@Table(name = "ls_link_node")
class LinkNode (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "link_node_id", nullable = false)
    val nodeSeq: Long = 0,

    @Column(name = "tier", nullable = false)
    val tier: Int = 0,

    @Column(name = "url", nullable = false)
    val url: String = "",

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "git_repo_seq", nullable = false)
    val repository: GitRepository = GitRepository(),

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_seq", nullable = false)
    val post: Post = Post(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_seq", nullable = false)
    val order: Order = Order(),

    val parentNodeSeq: Long? = null
): BaseTimeEntity()
