package com.sushistack.linktree.entity.content

import com.sushistack.linktree.entity.BaseTimeEntity
import com.sushistack.linktree.entity.link.LinkNode
import jakarta.persistence.*

@Entity
@Table(name = "lt_publication")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "publication_type", discriminatorType = DiscriminatorType.STRING)
class Publication (
    @Id
    @Column(name = "publication_seq", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val publicationSeq: Long = 0,

    @OneToOne(mappedBy = "publication", fetch = FetchType.LAZY)
    var linkNode: LinkNode? = null
): BaseTimeEntity()