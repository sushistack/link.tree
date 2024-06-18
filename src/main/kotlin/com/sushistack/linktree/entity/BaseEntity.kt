package com.sushistack.linktree.entity

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseEntity : BaseTimeEntity() {
    @CreatedBy
    @Column(updatable = false)
    var createdBy: String = ""

    @LastModifiedBy
    var lastModifiedBy: String = ""
}