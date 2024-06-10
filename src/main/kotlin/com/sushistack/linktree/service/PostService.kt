package com.sushistack.linktree.service

import com.sushistack.linktree.config.aop.annotations.GitTransactional
import com.sushistack.linktree.entity.content.Post
import com.sushistack.linktree.external.crawler.model.Article
import com.sushistack.linktree.external.git.addAndCommit
import com.sushistack.linktree.external.git.push
import com.sushistack.linktree.model.getMinUsed
import com.sushistack.linktree.repository.content.PostRepository
import com.sushistack.linktree.utils.ArticleUtils
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream

@Service
class PostService(
    private val appHomeDir: String,
    private val gitFactory: GitFactory,
    private val postRepository: PostRepository,
    private val articleService: ArticleService
) {
    companion object {

    }

    @GitTransactional
    fun createPost(post: Post, keywords: List<String>): Post {
        val repo = post.webpage.repository
        val gitAccount = repo.gitAccount

        val git = gitFactory.openRepo(appHomeDir, repo.workspaceName, repo.repositoryName, gitAccount.appPassword)

        this.write(post, keywords)

        with(git) {
            addAndCommit()
            push(username = gitAccount.username, appPassword = gitAccount.appPassword)
        }

        val savedPost = postRepository.save(post)
        return savedPost
    }

    // consider suspend
    private fun write(post: Post, keywords: List<String>) {
        val articleSources = keywords.flatMap { articleService.getArticleSources(it) }
        val articleSource = articleSources.getMinUsed()
        require(articleSource != null) { "Article sources not available" }

        val article = ClassPathResource(articleSource.get())
            .file
            .readText()
            .let { Json.decodeFromString<Article>(it) }
            .let { ArticleUtils.markdownify(it) }

        val fullFilePath = post.getLocalFileFullPath(appHomeDir)
        article.byteInputStream().use { input ->
            File(fullFilePath).outputStream().use {
                FileOutputStream(fullFilePath).use { output -> input.copyTo(output) }
            }
        }
    }
}