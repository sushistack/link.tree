package com.sushistack.linktree.service

import com.sushistack.linktree.config.measure.MeasureTime
import com.sushistack.linktree.config.transaction.TransactionCallbackHandler
import com.sushistack.linktree.entity.content.Post
import com.sushistack.linktree.entity.publisher.StaticWebpage
import com.sushistack.linktree.external.crawler.model.Article
import com.sushistack.linktree.jobs.link.gen.service.LinkProvider
import com.sushistack.linktree.model.ArticleSource
import com.sushistack.linktree.model.getMinUsed
import com.sushistack.linktree.repository.content.PostRepository
import com.sushistack.linktree.utils.ArticleUtils
import com.sushistack.linktree.utils.DateRange
import com.sushistack.linktree.utils.git.*
import com.sushistack.linktree.utils.git.enums.ResetType
import com.sushistack.linktree.utils.pick
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.File
import java.io.FileOutputStream

@Service
class PostService(
    private val appHomeDir: String,
    private val postRepository: PostRepository,
    private val txCallbackHandler: TransactionCallbackHandler
) {
    private val log = KotlinLogging.logger {}

    companion object {
        private val DATE_RANGE = DateRange()
    }

    @MeasureTime
    @Transactional(rollbackFor = [Exception::class])
    @Retryable(value = [Exception::class], maxAttempts = 3, backoff = Backoff(delay = 2000, multiplier = 2.0))
    fun createPost(webpage: StaticWebpage, articleSources: List<ArticleSource>, linkProvider: LinkProvider): Post {
        val repo = webpage.repository!!
        val git = Git(appHomeDir, repo.workspaceName, repo.repositoryName)
        git.checkout(branch = Git.DEFAULT_BRANCH)
        val commitId = git.getCommitId()
        val hash = if (commitId.isNotBlank()) "${commitId}-" else ""

        txCallbackHandler.registerCallback(
            git,
            onCommit = { g ->  },
            onRollback = { g -> g.reset(type = ResetType.HARD, hash = commitId) }
        )

        val (anchorText, url) = linkProvider.get()
        val postName = "${hash}${anchorText.replace(" ", "-")}"
        val uri = "life/${postName}.html"
        val filePath = "life/_posts/${DATE_RANGE.pick()}-${postName}.md"
        val post = Post(filePath, uri, webpage)

        this.write(post, articleSources, anchorText to url)
        git.addAll()
        git.commit("Add Post")
        return postRepository.save(post)
    }

    @MeasureTime
    fun write(post: Post, articles: List<ArticleSource>, link: Pair<String, String>) {
        val articleSource = articles.getMinUsed()
        require(articleSource != null) { "Article source not available" }

        val article = File(articleSource.get())
            .readText()
            .let { Json.decodeFromString<Article>(it) }
            .also {
                it.content = ArticleUtils.removeConsonantsAndGathers(it.content)
                it.content = ArticleUtils.spinSynonyms(it.content)
                it.content = ArticleUtils.inject(it.content, link)
            }
            .let { ArticleUtils.markdownify(it) }

        val fullFilePath = post.getLocalFileFullPath(appHomeDir)
        article.byteInputStream().use { input ->
            val file = File(fullFilePath)
            file.parentFile?.let { parentDir ->
                if (!parentDir.exists()) {
                    parentDir.mkdirs()
                }
            }
            file.outputStream().use {
                FileOutputStream(fullFilePath).use { output -> input.copyTo(output) }
            }
        }
    }
}