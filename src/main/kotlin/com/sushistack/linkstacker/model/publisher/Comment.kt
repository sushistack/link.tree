package com.sushistack.linkstacker.model.publisher

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("comment")
class Comment (
    @Id val id: String? = null,
    val publisherType: PublisherType,
    val postUrl: String
)