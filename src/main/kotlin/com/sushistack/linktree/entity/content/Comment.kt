package com.sushistack.linktree.entity.content

import com.sushistack.linktree.entity.link.LinkNode
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue(PublicationType.COMMENT_DISCRIMINATOR)
class Comment (
    @Column(name = "post_url")
    val postUrl: String? = null,

    linkNode: LinkNode? = null
) : Publication(linkNode = linkNode)