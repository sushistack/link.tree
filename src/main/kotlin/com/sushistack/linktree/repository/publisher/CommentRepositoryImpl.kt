package com.sushistack.linktree.repository.publisher

import com.querydsl.jpa.impl.JPAQueryFactory
import com.sushistack.linktree.entity.publisher.Comment
import com.sushistack.linktree.entity.publisher.QComment.comment

class CommentRepositoryImpl(private val queryFactory: JPAQueryFactory): CommentRepositoryCustom {

    override fun findByOrderByUsedCountLimit(limit: Long): List<Comment> =
        queryFactory
            .selectFrom(comment)
            .orderBy(comment.usedCount.asc())
            .limit(limit)
            .fetch()

}