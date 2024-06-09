package com.sushistack.linktree.service

import com.sushistack.linktree.config.aop.annotations.GitTransactional
import com.sushistack.linktree.entity.content.Post
import com.sushistack.linktree.external.git.addAndCommit
import com.sushistack.linktree.external.git.push
import com.sushistack.linktree.repository.content.PostRepository
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream

@Service
class PostService(
    private val appHomeDir: String,
    private val gitFactory: GitFactory,
    private val postRepository: PostRepository
) {

    @GitTransactional
    fun createPost(post: Post): Post {
        val repo = post.webpage.repository
        val gitAccount = repo.gitAccount

        val git = gitFactory.openRepo(appHomeDir, repo.workspaceName, repo.repositoryName, gitAccount.appPassword)

        this.write(post)

        with(git) {
            addAndCommit()
            push(username = gitAccount.username, appPassword = gitAccount.appPassword)
        }

        val savedPost = postRepository.save(post)
        return savedPost
    }

    // consider suspend
    private fun write(post: Post) {
        val content = "abcdef"
        val fullFilePath = post.getLocalFileFullPath(appHomeDir)
        content.byteInputStream().use { input ->
            File(fullFilePath).outputStream().use {
                FileOutputStream(fullFilePath).use { output -> input.copyTo(output) }
            }
        }
    }
}