package com.sushistack.linkstacker.model.link

import com.sushistack.linkstacker.model.content.Post
import com.sushistack.linkstacker.model.git.GitRepository
import com.sushistack.linkstacker.model.order.Order
import jakarta.persistence.*

@Entity
@Table(name = "ls_link_node")
class LinkNode (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "link_node_id", nullable = false)
    val nodeSeq: Long = 0,

    @Column(name = "link_node_id", nullable = false)
    val tier: Int = 0,

    @Column(name = "link_node_id", nullable = false)
    val url: String = "",

    @OneToOne
    @Column(name = "repository", nullable = false)
    val repository: GitRepository = GitRepository.empty(),

    @OneToOne
    @Column(name = "post", nullable = false)
    val post: Post = Post.empty(),

    @OneToOne
    @Column(name = "order", nullable = false)
    val order: Order = Order.empty(),

    val parentNodeSeq: Long? = null
)
