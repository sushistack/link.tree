package com.sushistack.linktree.utils

import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class ArticleUtilsTest {

    @ParameterizedTest(name = "[{index}] content = [{0}]")
    @MethodSource("contentsProvider")
    fun removeConsonantsAndGathersTest(content: String, expected: Boolean) {
        // Then
        val result = ArticleUtils.removeConsonantsAndGathers(content)
        Assertions.assertThat(result == content).isEqualTo(expected)
    }

    @ParameterizedTest(name = "[{index}] content = [{0}]")
    @MethodSource("spinContentsProvider")
    fun spinSynonymsTest(content: String, expected: Boolean) {
        // Then
        val result = ArticleUtils.spinSynonyms(content)
        Assertions.assertThat(result != content).isEqualTo(expected)
    }

    companion object {
        @JvmStatic
        fun contentsProvider() = listOf(
            Arguments.of("가나다라마", true),
            Arguments.of("고고고ㄱ고고", false),
            Arguments.of("ㅏㅏ카캄ㅁㅁ댜", false),
            Arguments.of("ㅓㅓㅓ매대알ㄹ갹", false)
        )

        @JvmStatic
        fun spinContentsProvider() = listOf(
            Arguments.of("abcd efg 문화 hijk", true),
            Arguments.of("그래서 hahaha", true),
            Arguments.of("hahaha", false),
        )
    }
}