package com.sushistack.linktree.jobs.post.config

import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

@Configuration
class PostConfig {
    private val threadCounters = ConcurrentHashMap<String, AtomicInteger>()

    @Bean
    fun postGenerationReqPool() =
        Executors.newFixedThreadPool(2)
            .asCoroutineDispatcher()

    @Bean("pbnDispatcher")
    fun pbnDispatcher() =
        Executors.newFixedThreadPool(4, namedThreadFactory("PBN-worker"))
            .asCoroutineDispatcher()

    @Bean("cbnDispatcher")
    fun cbnDispatcher() =
        Executors.newFixedThreadPool(8, namedThreadFactory("CBN-worker"))
            .asCoroutineDispatcher()

    private fun namedThreadFactory(prefix: String): ThreadFactory =
        ThreadFactory { r ->
            val counter = threadCounters.computeIfAbsent(prefix) { AtomicInteger(1) }
            Thread(r, "$prefix-${counter.getAndIncrement()}")
        }
}