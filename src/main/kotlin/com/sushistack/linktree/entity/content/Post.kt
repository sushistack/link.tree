package com.sushistack.linktree.entity.content

import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.publisher.StaticWebpage
import jakarta.persistence.*

@Entity
@DiscriminatorValue(PublicationType.POST_DISCRIMINATOR)
class Post (
    @Column(name = "file_path")
    val filePath: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webpage_seq")
    val webpage: StaticWebpage? = null,

    linkNode: LinkNode? = null
): Publication(linkNode = linkNode) {

    fun getLocalFileFullPath(appHomeDir: String): String =
        "${appHomeDir}/repo/${webpage?.repository?.workspaceName}/${webpage?.repository?.repositoryName}/${getFilename()}"

    fun getFilename(): String = filePath?.split("/")?.last() ?: ""

}