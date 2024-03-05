package com.sushistack.linktree.entity.content

import com.sushistack.linktree.entity.BaseTimeEntity
import com.sushistack.linktree.entity.publisher.StaticWebpage
import jakarta.persistence.*

@Entity
@Table(name = "lt_post")
class Post (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_seq", nullable = false)
    val postSeq: Long = 0,

    @Column(name = "file_path", nullable = false)
    val filePath: String = "",

    @Column(name = "file_name", nullable = false)
    val fileName: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webpage_seq", nullable = false)
    val webpage: StaticWebpage = StaticWebpage(),
): BaseTimeEntity()