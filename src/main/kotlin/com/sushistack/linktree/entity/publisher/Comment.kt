package com.sushistack.linktree.entity.publisher

import com.sushistack.linktree.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "lt_comment")
class Comment (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_seq", nullable = false)
    val comment_seq: Long = 0,

    @Column(name = "post_url", nullable = false)
    val postUrl: String = ""
): BaseTimeEntity()