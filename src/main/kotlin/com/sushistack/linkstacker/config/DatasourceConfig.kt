package com.sushistack.linkstacker.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class DatasourceConfig(private val mongoTemplate: MongoTemplate) {
    @Bean
    fun mongoTransactionManager(): MongoTransactionManager =
        MongoTransactionManager(mongoTemplate.mongoDatabaseFactory)
}