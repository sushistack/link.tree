package com.sushistack.linktree.entity.publisher

import com.sushistack.linktree.entity.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "lt_comment")
class CommentableWebpage (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_seq", nullable = false)
    val commentSeq: Long = 0,

    @Column(name = "post_url", nullable = false)
    val postUrl: String = "",

    @Column(name = "used_count", nullable = false)
    val usedCount: Int = 0,
): BaseTimeEntity() {
    override fun toString(): String = "CommentableWebpage(commentSeq=$commentSeq, postUrl=$postUrl, usedCount=$usedCount)"
}