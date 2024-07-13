package com.sushistack.linktree.repository.link

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import com.sushistack.linktree.entity.content.QPost.post
import com.sushistack.linktree.entity.content.QPublication.publication
import com.sushistack.linktree.entity.git.QGitAccount.gitAccount
import com.sushistack.linktree.entity.git.QGitRepository.gitRepository
import com.sushistack.linktree.entity.link.LinkNode
import com.sushistack.linktree.entity.link.QLinkNode.linkNode
import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.order.QOrder
import com.sushistack.linktree.entity.publisher.QStaticWebpage.staticWebpage
import com.sushistack.linktree.model.dto.LinkNodeRepositoryDTO

class LinkNodeRepositoryImpl(private val queryFactory: JPAQueryFactory): LinkNodeRepositoryCustom {
    override fun findByOrderAndTier(order: Order, tier: Int): List<LinkNode> =
        queryFactory
            .selectFrom(linkNode)
            .join(linkNode.order, QOrder.order).fetchJoin()
            .join(linkNode.publication, publication).fetchJoin()
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
            .where(
                linkNode.tier.eq(tier)
                    .and(linkNode.order.orderSeq.eq(QOrder.order.orderSeq))
                    .and(QOrder.order.orderSeq.eq(order.orderSeq))
            ).fetch()

    override fun findWithPostByOrder(order: Order, tier: Int): List<LinkNodeRepositoryDTO> =
        queryFactory
            .select(
                Projections.constructor(
                    LinkNodeRepositoryDTO::class.java,
                    linkNode.nodeSeq,
                    linkNode.tier,
                    gitRepository.repositorySeq,
                    gitRepository.workspaceName,
                    gitRepository.repositoryName,
                    staticWebpage.domain,
                    post.uri,
                    gitAccount.accountSeq,
                    gitAccount.username,
                    gitAccount.appPassword
                )
            )
            .from(linkNode)
            .join(linkNode.order, QOrder.order)
            .join(linkNode.publication, publication)
            .join(post).on(publication.publicationSeq.eq(post.publicationSeq))
            .join(post.webpage, staticWebpage)
            .join(staticWebpage.repository, gitRepository)
            .join(gitRepository.gitAccount, gitAccount)
            .distinct()
            .where(
                linkNode.tier.eq(tier)
                    .and(linkNode.order.orderSeq.eq(QOrder.order.orderSeq))
                    .and(QOrder.order.orderSeq.eq(order.orderSeq))
            )
            .fetch()
}