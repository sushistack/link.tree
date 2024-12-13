package com.sushistack.linktree.jobs.post.config

import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executors

@Configuration
class PostConfig {

    @Bean
    fun postGenerationReqPool() =
        Executors.newFixedThreadPool(2)
            .asCoroutineDispatcher()

    @Bean
    fun postBuildAndDeployPool() =
        Executors.newFixedThreadPool(8)
            .asCoroutineDispatcher()
}