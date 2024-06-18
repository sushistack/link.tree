package com.sushistack.linktree.entity.content

import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.publisher.CommentableWebpage
import jakarta.persistence.*

@Entity
@DiscriminatorValue(PublicationType.COMMENT_DISCRIMINATOR)
class Comment (
    @Column(name = "post_url")
    val postUrl: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_seq")
    val commentableWebpage: CommentableWebpage? = null,

    linkNode: LinkNode? = null
) : Publication(linkNode = linkNode)