package com.sushistack.linktree.utils

import kotlinx.serialization.json.Json
import org.springframework.core.io.ClassPathResource
import java.nio.charset.StandardCharsets

class ArticleUtils {
    companion object {
        private const val SYNONYMS_FILE_PATH = "synonyms.json"
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
    }
}
