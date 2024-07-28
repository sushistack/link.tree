package com.sushistack.linktree.service

import com.sushistack.linktree.config.transaction.TransactionCallbackHandler
import com.sushistack.linktree.entity.content.Post
import com.sushistack.linktree.entity.publisher.StaticWebpage
import com.sushistack.linktree.external.crawler.model.Article
import com.sushistack.linktree.external.git.addAndCommit
import com.sushistack.linktree.external.git.getCommitId
import com.sushistack.linktree.external.git.push
import com.sushistack.linktree.external.git.resetTo
import com.sushistack.linktree.jobs.link.gen.service.LinkProvider
import com.sushistack.linktree.model.ArticleSource
import com.sushistack.linktree.model.getMinUsed
import com.sushistack.linktree.repository.content.PostRepository
import com.sushistack.linktree.utils.ArticleUtils
import com.sushistack.linktree.utils.DateRange
import com.sushistack.linktree.utils.pick
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
    private val gitFactory: GitFactory,
    private val postRepository: PostRepository,
    private val txCallbackHandler: TransactionCallbackHandler
) {
    companion object {
        private val DATE_RANGE = DateRange()
    }

    @Transactional(rollbackFor = [Exception::class])
    @Retryable(value = [Exception::class], maxAttempts = 3, backoff = Backoff(delay = 2000, multiplier = 2.0))
    fun createPost(webpage: StaticWebpage, articleSources: List<ArticleSource>, linkProvider: LinkProvider): Post {
        val repo = webpage.repository!!
        val gitAccount = repo.gitAccount!!
        val git = gitFactory.openRepo(appHomeDir, repo.workspaceName, repo.repositoryName, gitAccount.appPassword)
        val commitId = git.getCommitId()
        val shortCommitId = commitId?.substring(0, 7) ?: ""
        val hash = if (shortCommitId.isNotBlank()) "${shortCommitId}-" else ""


        txCallbackHandler.registerCallback(
            git,
            onCommit = { g ->
                g.push(username = gitAccount.username, appPassword = gitAccount.appPassword)
                g.close()
            },
            onRollback = { g ->
                g.resetTo(commitId = commitId ?: "HEAD")
                g.close()
            }
        )

        val (anchorText, url) = linkProvider.get()
        val postName = "${hash}${anchorText.replace(" ", "-")}"
        val uri = "life/${postName}"
        val filePath = "life/${DATE_RANGE.pick()}-${postName}.md"
        val post = Post(filePath, uri, webpage)

        this.write(post, articleSources, anchorText to url)
        git.addAndCommit()
        git.close()
        return postRepository.save(post)
    }

    private fun write(post: Post, articles: List<ArticleSource>, link: Pair<String, String>) {
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