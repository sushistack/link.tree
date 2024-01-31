package com.sushistack.linkstacker.entity.content

import com.sushistack.linkstacker.entity.BaseTimeEntity
import com.sushistack.linkstacker.entity.publisher.StaticWebpage
import jakarta.persistence.*

@Entity
class Post (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_seq", nullable = false)
    val postSeq: Long = 0,

    @Column(name = "file_path", nullable = false)
    val filePath: String = "",

    @Column(name = "file_name", nullable = false)
    val fileName: String = "",

    @OneToOne
    @JoinColumn(name = "webpage_seq", nullable = false)
    val webpage: StaticWebpage = StaticWebpage(),
): BaseTimeEntity()