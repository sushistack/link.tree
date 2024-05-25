package com.sushistack.linktree.repository.link

import com.querydsl.jpa.impl.JPAQueryFactory
import com.sushistack.linktree.entity.git.QGitAccount
import com.sushistack.linktree.entity.git.QGitRepository.gitRepository
import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.link.QLinkNode.linkNode
import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.order.QOrder
import com.sushistack.linktree.entity.publisher.QStaticWebpage

class LinkNodeRepositoryImpl(private val queryFactory: JPAQueryFactory): LinkNodeRepositoryCustom {
    override fun findByOrderAndTier(order: Order, tier: Int): List<LinkNode> =
        queryFactory
            .selectFrom(linkNode)
            .join(linkNode.order, QOrder.order).fetchJoin()
            .join(linkNode.repository, gitRepository).fetchJoin()
            .join(gitRepository.gitAccount, QGitAccount.gitAccount).fetchJoin()
            .where(
                linkNode.tier.eq(tier)
                    .and(linkNode.order.orderSeq.eq(QOrder.order.orderSeq))
                    .and(QOrder.order.orderSeq.eq(order.orderSeq))
            )
            .fetch()

    override fun findAllByOrderAndTier(order: Order, tier: Int): List<LinkNode> =
        queryFactory
            .selectFrom(linkNode)
            .join(linkNode.order, QOrder.order).fetchJoin()
            .join(linkNode.repository, gitRepository).fetchJoin()
            .join(gitRepository.webpage, QStaticWebpage.staticWebpage).fetchJoin()
            .where(
                linkNode.tier.eq(tier)
                    .and(linkNode.order.orderSeq.eq(QOrder.order.orderSeq))
                    .and(QOrder.order.orderSeq.eq(order.orderSeq))
            ).fetch()

}