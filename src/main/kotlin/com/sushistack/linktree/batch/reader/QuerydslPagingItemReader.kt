package com.sushistack.linktree.batch.reader

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.EntityManager
import jakarta.persistence.EntityManagerFactory
import org.springframework.batch.item.database.AbstractPagingItemReader
import java.util.concurrent.CopyOnWriteArrayList

open class QuerydslPagingItemReader<T>(
    private val entityManagerFactory: EntityManagerFactory,
    private val queryFunction: (JPAQueryFactory, Int, Int) -> List<T>
) : AbstractPagingItemReader<T>() {

    private lateinit var entityManager: EntityManager
    private lateinit var queryFactory: JPAQueryFactory

    init {
        this.pageSize = 100
        this.name = "QuerydslPagingItemReader"
    }

    override fun doOpen() {
        super.doOpen()
        entityManager = entityManagerFactory.createEntityManager()
        queryFactory = JPAQueryFactory(entityManager)
    }

    override fun doReadPage() {
        if (results == null) {
            results = CopyOnWriteArrayList()
        } else {
            results.clear()
        }

        val offset = page * pageSize
        val limit = pageSize

        val queryResults = queryFunction(queryFactory, offset, limit)

        queryResults.forEach { entityManager.detach(it) }

        results.addAll(queryResults)
    }

    override fun doClose() {
        entityManager.close()
        super.doClose()
    }
}