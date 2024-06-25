package com.sushistack.linktree.jobs.crawl.tasklet

import com.sushistack.linktree.external.crawler.CrawlService
import kotlinx.serialization.json.Json
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@JobScope
@Component
class CrawlTasklet(
    private val crawlService: CrawlService,
    @Value("#{jobParameters['keywords']}") private val keywordsJson: String
): Tasklet {
    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? {
        val keywords: List<String> = Json.decodeFromString(keywordsJson)
        crawlService.crawlArticles(keywords)
        return RepeatStatus.FINISHED
    }
}