package com.sushistack.linktree.repository.link

import com.querydsl.jpa.impl.JPAQueryFactory
import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.link.QLinkNode.linkNode
import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.order.QOrder

class LinkNodeRepositoryImpl(private val queryFactory: JPAQueryFactory): LinkNodeRepositoryCustom {
    override fun findByOrderAndTier(order: Order, tier: Int): List<LinkNode> =
        queryFactory
            .selectFrom(linkNode)
            .innerJoin(QOrder.order)
            .on(linkNode.order.orderSeq.eq(QOrder.order.orderSeq).and(QOrder.order.orderSeq.eq(order.orderSeq)))
            .where(linkNode.tier.eq(tier))
            .fetch()
}