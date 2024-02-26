package com.sushistack.linktree.utils

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class ArticleUtilsTest {

    @ParameterizedTest(name = "[{index}] content = [{0}]")
    @MethodSource("contentsProvider")
    fun removeConsonantsAndGathersTest(content: String, expected: Boolean) {
        // Then
        val result = ArticleUtils.removeConsonantsAndGathers(content)
        Assertions.assertThat(result == content).isEqualTo(expected)
    }

    @Disabled
    @ParameterizedTest(name = "[{index}] content = [{0}]")
    @MethodSource("spinContentsProvider")
    fun spinSynonymsTest(content: String, synonym: String, expected: String) {
        // Given
        val s = Mockito.mock(List::class.java)

        // When
        `when`(s.random()).thenReturn(synonym)

        // Then
        val result = ArticleUtils.spinSynonyms(content)
        Assertions.assertThat(result).isEqualTo(expected)
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
            Arguments.of("abcd efg 문화 hijk", "문명", "abcd efg 문명 hijk"),
            Arguments.of("그래서 hahaha", "그러면", "그래서 hahaha")
        )
    }
}