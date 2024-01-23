package com.sushistack.linkstacker.config

import org.springframework.batch.core.configuration.JobRegistry
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.batch.core.explore.JobExplorer
import org.springframework.batch.core.explore.support.SimpleJobExplorer
import org.springframework.batch.core.launch.JobLauncher
import org.springframework.batch.core.launch.JobOperator
import org.springframework.batch.core.launch.support.SimpleJobOperator
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.experimental.core.repository.dao.MongoExecutionContextDao
import org.springframework.batch.experimental.core.repository.dao.MongoJobExecutionDao
import org.springframework.batch.experimental.core.repository.dao.MongoJobInstanceDao
import org.springframework.batch.experimental.core.repository.dao.MongoStepExecutionDao
import org.springframework.batch.experimental.core.repository.support.MongoJobRepositoryFactoryBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.transaction.annotation.Isolation


@Configuration
@EnableBatchProcessing
class BatchConfig(
    private val mongoTemplate: MongoTemplate,
    private val mongoTransactionManager: MongoTransactionManager
) {

    @Bean
    fun jobRepository() : JobRepository =
        MongoJobRepositoryFactoryBean().also {
            it.setMongoOperations(mongoTemplate)
            it.transactionManager = mongoTransactionManager
            it.setIsolationLevelForCreateEnum(Isolation.SERIALIZABLE)
            it.afterPropertiesSet()
        }.`object`

    @Bean
    fun jobExplorer(): JobExplorer = SimpleJobExplorer(
        MongoJobInstanceDao(mongoTemplate),
        MongoJobExecutionDao(mongoTemplate),
        MongoStepExecutionDao(mongoTemplate),
        MongoExecutionContextDao(mongoTemplate)
    )

    @Bean
    fun taskExecutor(): ThreadPoolTaskExecutor =
        ThreadPoolTaskExecutor().also {
            it.corePoolSize = 5
            it.maxPoolSize = 10
            it.queueCapacity = 25
            it.initialize()
        }

    @Bean
    fun jobLauncher(): JobLauncher =
        TaskExecutorJobLauncher().also {
            it.setJobRepository(jobRepository())
            it.setTaskExecutor(taskExecutor())
        }

    @Bean
    fun jobOperator(jobRegistry: JobRegistry): JobOperator =
        SimpleJobOperator().also {
            it.setJobExplorer(jobExplorer())
            it.setJobRegistry(jobRegistry)
            it.setJobLauncher(jobLauncher())
            it.setJobRepository(jobRepository())
        }
}