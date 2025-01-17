package com.sushistack.linktree.repository.publisher

import com.querydsl.jpa.impl.JPAQueryFactory
import com.sushistack.linktree.entity.content.QPost.post
import com.sushistack.linktree.entity.content.QPublication.publication
import com.sushistack.linktree.entity.git.QGitAccount.gitAccount
import com.sushistack.linktree.entity.git.QGitRepository.gitRepository
import com.sushistack.linktree.entity.link.QLinkNode.linkNode
import com.sushistack.linktree.entity.order.Order
import com.sushistack.linktree.entity.publisher.QStaticWebpage.staticWebpage
import com.sushistack.linktree.entity.publisher.ServiceProviderType
import com.sushistack.linktree.entity.publisher.StaticWebpage

class StaticWebpageRepositoryImpl(private val queryFactory: JPAQueryFactory): StaticWebpageRepositoryCustom {

    override fun findAllByProviderType(providerType: ServiceProviderType): List<StaticWebpage> =
        queryFactory
            .selectFrom(staticWebpage)
            .join(staticWebpage.repository, gitRepository).fetchJoin()
            .join(gitRepository.gitAccount, gitAccount).fetchJoin()
            .where(staticWebpage.providerType.eq(providerType))
            .fetch()

    override fun findAllByOrderAndProviderType(order: Order, providerType: ServiceProviderType): List<StaticWebpage> =
        queryFactory
            .select(staticWebpage)
            .from(linkNode)
            .join(linkNode.publication, publication)
            .join(post).on(publication.publicationSeq.eq(post.publicationSeq))
            .join(post.webpage, staticWebpage)
            .join(staticWebpage.repository, gitRepository).fetchJoin()
            .where(linkNode.order.eq(order).and(staticWebpage.providerType.eq(providerType)))
            .fetch()
}