package com.sushistack.linktree.jobs.post.service

import com.sushistack.linktree.model.Article
import com.sushistack.linktree.service.llm.impl.CohereService
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import java.io.FileOutputStream
import java.nio.file.Paths

@Service
class PostGenerationService(
    private val appHomeDir: String,
    private val cohereService: CohereService,
    private val postGenerationReqPool: ExecutorCoroutineDispatcher
) {
    private val log = KotlinLogging.logger {}

    companion object {
        val titleHolders = listOf("{{title}} 이란?", "{{title}} 알아 보기", "{{title}}, 무엇인가?")
        val descriptionHolders = listOf("{{desc}} 에 대해서 알아 보는 시간을 가져 보겠습니다.", "{{desc}} 란 무엇일까요?", "{{desc}}, 왜 알아야 하는가?")
    }

    suspend fun generatePost(keyword: String, index: Int) = withContext(postGenerationReqPool) {
        log.info { "Generating post for $keyword" }
        val query = "${keyword}에 대한 600 ~ 800 자 한국어 기사 작성"
        val title = titleHolders.shuffled().first().replace("{{title}}", keyword)
        val description = descriptionHolders.shuffled().first().replace("{{desc}}", keyword)
        val content = cohereService.call(query)

        if (content.isNotBlank()) {
            val article = Article(title, description, content)
            write(article, keyword, index)
        }
    }

    private fun write(article: Article, keyword: String, index: Int) {
        val articleJson = Json.encodeToString(Article.serializer(), article)
        val file = Paths.get("${appHomeDir}/files/articles/$keyword/$index.json").toFile()
        file.parentFile?.let { parentDir ->
            if (!parentDir.exists()) {
                parentDir.mkdirs()
            }
        }
        FileOutputStream(file).use { outputStream ->
            outputStream.write(articleJson.toByteArray())
        }
    }
}