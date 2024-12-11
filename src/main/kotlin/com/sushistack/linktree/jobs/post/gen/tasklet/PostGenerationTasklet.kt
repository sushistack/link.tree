package com.sushistack.linktree.jobs.post.gen.tasklet

import com.sushistack.linktree.jobs.post.service.PostGenerationService
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.springframework.batch.core.StepContribution
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.batch.core.scope.context.ChunkContext
import org.springframework.batch.core.step.tasklet.Tasklet
import org.springframework.batch.repeat.RepeatStatus
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.file.Paths
import kotlin.io.path.exists

@JobScope
@Component
class PostGenerationTasklet(
    private val appHomeDir: String,
    private val postGenerationService: PostGenerationService,
    @Value("#{jobParameters['keywords']}") private val keywordsJson: String
): Tasklet {
    private val log = KotlinLogging.logger {}
    companion object {
        const val ARTICLE_COUNT = 30
    }

    override fun execute(contribution: StepContribution, chunkContext: ChunkContext): RepeatStatus? = runBlocking {
        val keywords: List<String> = Json.decodeFromString(keywordsJson)
        (0..ARTICLE_COUNT).map {
            async {
                val keyword = keywords.shuffled().first()
                postGenerationService.generatePost(keyword, it)
            }
        }.awaitAll()

        RepeatStatus.FINISHED
    }
}