package com.sushistack.linktree.utils

import com.sushistack.linktree.model.Article
import kotlinx.serialization.json.Json
import org.springframework.core.io.ClassPathResource
import java.nio.charset.StandardCharsets
import kotlin.random.Random

class ArticleUtils {
    companion object {
        private const val SYNONYMS_FILE_PATH = "synonyms.json"
        private const val MARKDOWN_TEMPLATE = "templates/post-template.md"
        private val consonantsAndGathers = listOf(
            "ㄱ", "ㄴ", "ㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅅ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ", "ㄲ", "ㄸ", "ㅃ", "ㅆ", "ㅉ",
            "ㅏ", "ㅑ", "ㅓ", "ㅕ", "ㅜ", "ㅠ", "ㅗ", "ㅛ", "ㅡ", "ㅣ", "ㅐ", "ㅔ", "ㅟ", "ㅢ", "ㅙ", "ㅝ", "ㅞ", "ㅒ", "ㅖ"
        )

        fun removeConsonantsAndGathers(content: String): String {
            var newContent: String = content
            for (koChar in consonantsAndGathers) {
                newContent = newContent.replace(koChar, "")
            }
            return newContent
        }

        fun spinSynonyms(content: String): String {
            val synonyms = ClassPathResource(SYNONYMS_FILE_PATH)
                .inputStream.readBytes()
                .toString(StandardCharsets.UTF_8)
                .let { Json.decodeFromString<List<List<String>>>(it) }

            return content.split(" ").joinToString(" ") { word ->
                for (s in synonyms) {
                    if (s.contains(word)) {
                        return@joinToString s.filter { it != word }.random()
                    }
                }
                return@joinToString word
            }
        }

        fun inject(content: String, link: Pair<String, String>): String =
            content
                .split(" ")
                .toMutableList()
                .apply {
                    val markdownLink = " [${link.first}](${link.second}) "
                    val index = indexOfFirst { it == link.first }
                    if (index != -1) {
                        this[index] = markdownLink
                    } else {
                        this.add(Random.nextInt(size + 1), markdownLink)
                    }
                }
                .joinToString(" ")

        fun markdownify(article: Article) = ClassPathResource(MARKDOWN_TEMPLATE)
            .inputStream.readBytes()
            .toString(StandardCharsets.UTF_8)
            .replace("{{title}}", article.getSafeTitle())
            .replace("{{content}}", article.content)
    }
}
