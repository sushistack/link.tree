package com.sushistack.linktree.service

import com.sushistack.linktree.entity.publisher.CommentableWebpage
import com.sushistack.linktree.repository.publisher.CommentableWebpageRepository
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class CommentableWebpageService(private val commentableWebpageRepository: CommentableWebpageRepository) {

    fun findBySeed(seed: Long, fixedSize: Int = 3): List<CommentableWebpage> {
        val webpages = commentableWebpageRepository.findAll()
        val random = Random(seed)
        val step = webpages.size / fixedSize

        return (0 until fixedSize)
            .map { i -> webpages[(random.nextInt(webpages.size) + i * step) % webpages.size] }
    }

}