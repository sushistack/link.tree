package com.sushistack.linktree.service

import com.sushistack.linktree.entity.content.Post
import com.sushistack.linktree.repository.content.PostRepository
import org.springframework.stereotype.Service

@Service
class PostService(private val postRepository: PostRepository) {

    fun createPost(post: Post): Post =
        postRepository.save(post)

}