package com.sushistack.linkstacker.jobs

import org.springframework.batch.core.Job
import org.springframework.batch.core.JobExecutionListener
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.item.ItemProcessor
import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.ItemWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class SampleJobConfig{

    @Bean
    fun sampleJob(jobRepository: JobRepository, step: Step, listener: JobExecutionListener): Job {
        return JobBuilder("sampleJob", jobRepository)
            .start(step)
            .listener(listener)
            .build()
    }

    @Bean
    fun sampleStep(
        jobRepository: JobRepository,
        transactionManager: PlatformTransactionManager,
        itemReader: ItemReader<Int>,
        itemProcessor: ItemProcessor<Int, Int>,
        itemWriter: ItemWriter<Int>
    ): Step {
        return StepBuilder("sampleStep", jobRepository)
            .chunk<Int, Int>(10, transactionManager)
            .reader(itemReader)
            .processor(itemProcessor)
            .writer(itemWriter)
            .build()
    }

    @Bean
    fun itemReader(): ItemReader<Int> {
        return ItemReader { (1..10).iterator().next() }
    }

    @Bean
    fun itemProcessor(): ItemProcessor<Int, Int> {
        return ItemProcessor { item ->
            item.times(2)
        }
    }

    @Bean
    fun itemWriter(): ItemWriter<Int> {
        return ItemWriter { items -> println("Processed items: $items") }
    }
}