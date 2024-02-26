package com.sushistack.linktree.utils

class ArticleUtils {
    companion object {
        val consonantsAndGathers = listOf(
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

    }
}