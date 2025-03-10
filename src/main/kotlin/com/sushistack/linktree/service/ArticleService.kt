package com.sushistack.linktree.service

import com.sushistack.linktree.model.ArticleSource
import org.springframework.stereotype.Service
import java.nio.file.Paths

@Service
class ArticleService(private val appHomeDir: String) {
    companion object {
        private const val ARTICLE_PATH = "files/articles"
    }

    fun getArticleSources(keyword: String): List<ArticleSource> {
        val directory = Paths.get("$appHomeDir/$ARTICLE_PATH/$keyword").toFile()
        require(directory.exists()) { "Directory does not exist: $directory" }
        require(directory.isDirectory) { "$directory is not a directory" }

        return directory.listFiles { file -> file.isFile }
            ?.map { file -> ArticleSource("$appHomeDir/$ARTICLE_PATH/$keyword/${file.name}") }
            ?: emptyList()
    }

}

