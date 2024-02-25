package com.sushistack.linktree.external.crawler

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import io.github.oshai.kotlinlogging.KotlinLogging
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

class CrawlServiceTest {

    private val log = KotlinLogging.logger {}
    private val crawlService: CrawlService = CrawlService()

    @DisplayName("Go to page by Playwright.")
    @ParameterizedTest(name = "[{index}] url = [{0}]")
    @ValueSource(strings = ["https://example.com", "https://www.aaa.com"])
    fun goTo(url: String) {
        Playwright.create().use { playwright ->
            val browser: Browser = playwright.chromium().launch(
                BrowserType.LaunchOptions().setHeadless(true)
            )
            val page = browser.newPage()
            page.navigate(url)
            Assertions.assertThat(page.title()).isNotNull()
            browser.close()
        }
    }

    @Disabled("It is so slow test.")
    @DisplayName("CrawlArticle Test.")
    @ParameterizedTest(name = "[{index}] url = [{0}]")
    @ValueSource(strings = ["https://43in.tistory.com/517", "https://blog.naver.com/dydtmd4/223610605424"])
    fun crawlArticleTest(url: String) {
        log.info { "content = ${crawlService.crawlArticle(url)}" }
    }

    @Disabled("It is so slow test.")
    @DisplayName("CrawlArticles Test.")
    @ParameterizedTest(name = "[{index}] keyword = [{0}]")
    @MethodSource("keywordsProvider")
    fun crawlArticlesTest(keywords: List<String>) {
        crawlService.crawlArticles(keywords)
    }

    companion object {
        @JvmStatic
        fun keywordsProvider() = listOf(
            listOf("감자의 효능", "토마토의 효능")
        )
    }

}
