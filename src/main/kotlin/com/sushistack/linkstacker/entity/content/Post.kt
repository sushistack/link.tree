package com.sushistack.linkstacker.model.content

import jakarta.persistence.*

@Entity
class Post (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "post_seq", nullable = false)
    val postSeq: Long = 0,

    @Column(columnDefinition = "file_path", nullable = false)
    val filePath: String = "",

    @Column(columnDefinition = "file_name", nullable = false)
    val fileName: String = "",

    @Column(columnDefinition = "publister_id", nullable = false)
    val publisherId: String = ""
) {
    companion object {
        fun empty() = Post()
    }

}