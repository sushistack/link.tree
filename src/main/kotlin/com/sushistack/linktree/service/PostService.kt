package com.sushistack.linktree.service

import com.sushistack.linktree.config.transaction.TransactionCallbackHandler
import com.sushistack.linktree.entity.content.Post
import com.sushistack.linktree.external.crawler.model.Article
import com.sushistack.linktree.external.git.addAndCommit
import com.sushistack.linktree.external.git.getCommitId
import com.sushistack.linktree.external.git.push
import com.sushistack.linktree.external.git.resetTo
import com.sushistack.linktree.model.ArticleSource
import com.sushistack.linktree.model.getMinUsed
import com.sushistack.linktree.repository.content.PostRepository
import com.sushistack.linktree.utils.ArticleUtils
import kotlinx.serialization.json.Json
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

    @Transactional
    fun createPost(post: Post, articleSources: List<ArticleSource>): Post {
        val repo = post.webpage!!.repository!!
        val gitAccount = repo.gitAccount!!
        val git = gitFactory.openRepo(appHomeDir, repo.workspaceName, repo.repositoryName, gitAccount.appPassword)
        val commitId = git.getCommitId()

        txCallbackHandler.registerCallback(
            git,
            onCommit = { g -> g.push(username = gitAccount.username, appPassword = gitAccount.appPassword) },
            onRollback = { g -> g.resetTo(commitId = commitId ?: "HEAD") }
        )

        this.write(post, articleSources)
        git.addAndCommit()

        val savedPost = postRepository.save(post)
        return savedPost
    }

    // consider suspend
    private fun write(post: Post, articles: List<ArticleSource>) {
        val articleSource = articles.getMinUsed()
        require(articleSource != null) { "Article source not available" }

        val article = File(articleSource.get())
            .readText()
            .let { Json.decodeFromString<Article>(it) }
            .also {
                it.content = ArticleUtils.removeConsonantsAndGathers(it.content)
                it.content = ArticleUtils.spinSynonyms(it.content)
            }
            .let { ArticleUtils.markdownify(it) }

        val fullFilePath = post.getLocalFileFullPath(appHomeDir)
        article.byteInputStream().use { input ->
            File(fullFilePath).outputStream().use {
                FileOutputStream(fullFilePath).use { output -> input.copyTo(output) }
            }
        }
    }
}