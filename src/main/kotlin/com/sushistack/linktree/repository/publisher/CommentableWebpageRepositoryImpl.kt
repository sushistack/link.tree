package com.sushistack.linktree.repository.publisher

import com.querydsl.jpa.impl.JPAQueryFactory
import com.sushistack.linktree.entity.publisher.CommentableWebpage
import com.sushistack.linktree.entity.publisher.QCommentableWebpage.commentableWebpage

class CommentableWebpageRepositoryImpl(private val queryFactory: JPAQueryFactory): CommentableWebpageRepositoryCustom {

    override fun findByOrderByUsedCountLimit(limit: Long): List<CommentableWebpage> =
        queryFactory
            .selectFrom(commentableWebpage)
            .orderBy(commentableWebpage.usedCount.asc())
            .limit(limit)
            .fetch()

}