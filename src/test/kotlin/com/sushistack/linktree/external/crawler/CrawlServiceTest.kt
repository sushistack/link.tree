package com.sushistack.linktree.external.crawler

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Playwright
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class CrawlServiceTest {

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
}
