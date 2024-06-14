package com.sushistack.linktree.utils

import com.sushistack.linktree.model.vo.GitAccountVO
import com.sushistack.linktree.model.vo.GitRepositoryVO
import com.sushistack.linktree.model.vo.StaticWebpageVO
import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class CsvUtilsTest {

    @ParameterizedTest
    @MethodSource("csvFilePathProvider")
    fun readTest(ga: String, gr: String, wp: String) {
        // When
        val gitAccounts: List<GitAccountVO> = CsvUtils.read(ga)
        val gitRepositories: List<GitRepositoryVO> = CsvUtils.read(gr)
        val webpages: List<StaticWebpageVO> = CsvUtils.read(wp)

        // Then
        Assertions.assertThat(gitAccounts).hasSize(3)
        Assertions.assertThat(gitRepositories).hasSize(3)
        Assertions.assertThat(webpages).hasSize(2)
    }

    @ParameterizedTest
    @MethodSource("csvDataProvider")
    fun readTextTest(ga: String, gr: String, wp: String) {

        // When
        val gitAccounts: List<GitAccountVO> = CsvUtils.readText(ga)
        val gitRepositories: List<GitRepositoryVO> = CsvUtils.readText(gr)
        val webpages: List<StaticWebpageVO> = CsvUtils.readText(wp)

        // Then
        Assertions.assertThat(gitAccounts).hasSize(3)
        Assertions.assertThat(gitRepositories).hasSize(3)
        Assertions.assertThat(webpages).hasSize(2)
    }

    companion object {
        @JvmStatic
        fun csvFilePathProvider() = listOf(
            Arguments.of("csv/ga.csv", "csv/gr.csv", "csv/wp.csv")
        )

        @JvmStatic
        fun csvDataProvider() = listOf(
            Arguments.of("""
                username,appPassword,hostingService
                a,b,GITHUB
                d,e,GITLAB
                g,h,BITBUCKET
                """.trimIndent(),
                """
                workspaceName,repositoryName
                a,b
                d,e
                g,h
                """.trimIndent(),
                """
                domain,providerType,usedCount
                a,PRIVATE_BLOG_NETWORK,0
                d,CLOUD_BLOG_NETWORK,11
                """.trimIndent()
            )
        )
    }
}