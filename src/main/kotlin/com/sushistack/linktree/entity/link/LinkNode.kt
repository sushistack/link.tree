package com.sushistack.linktree.entity.link

import com.sushistack.linktree.entity.BaseTimeEntity
import com.sushistack.linktree.entity.content.Post
import com.sushistack.linktree.entity.git.GitRepository
import com.sushistack.linktree.entity.order.Order
import jakarta.persistence.*

@Entity
@Table(name = "lt_link_node")
class LinkNode (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "link_node_id", nullable = false)
    val nodeSeq: Long = 0,

    @Column(name = "tier", nullable = false)
    val tier: Int = 0,

    @Column(name = "url", nullable = false)
    val url: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_seq")
    val repository: GitRepository? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_seq")
    val order: Order? = null,

    val parentNodeSeq: Long? = null
): BaseTimeEntity()
