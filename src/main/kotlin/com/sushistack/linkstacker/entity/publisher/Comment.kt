package com.sushistack.linkstacker.model.publisher

import jakarta.persistence.*

@Entity
@Table(name = "ls_comment")
class Comment (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_seq", nullable = false)
    val comment_seq: Long = 0,

    @Column(name = "post_url", nullable = false)
    val postUrl: String = ""
)