package com.sushistack.linktree.external.crawler

import com.microsoft.playwright.Browser
import com.microsoft.playwright.BrowserType
import com.microsoft.playwright.Locator
import com.microsoft.playwright.Playwright
import com.sushistack.linktree.external.crawler.CrawlVariables.Companion.ARTICLE_PAGE_TIMEOUT
import com.sushistack.linktree.external.crawler.CrawlVariables.Companion.MAX_PAGE
import com.sushistack.linktree.external.crawler.CrawlVariables.Companion.MIN_WORDS
import com.sushistack.linktree.external.crawler.CrawlVariables.Companion.MOBILE_UA
import com.sushistack.linktree.external.crawler.CrawlVariables.Companion.PC_UA
import com.sushistack.linktree.external.crawler.CrawlVariables.Companion.SEARCH_PAGE_TIMEOUT
import com.sushistack.linktree.external.crawler.CrawlVariables.Companion.articleCardsSelector
import com.sushistack.linktree.external.crawler.CrawlVariables.Companion.articleDescriptionSelector
import com.sushistack.linktree.external.crawler.CrawlVariables.Companion.articleSelectors
import com.sushistack.linktree.external.crawler.CrawlVariables.Companion.articleTitleSelector
import com.sushistack.linktree.external.crawler.CrawlVariables.Companion.searchUrl
import com.sushistack.linktree.external.crawler.model.Article
import kotlinx.serialization.json.Json
import java.io.FileOutputStream
import java.nio.file.Paths

class CrawlService {

    fun crawlArticles(keywords: List<String>) {
        Playwright.create().use { playwright ->
            val browser = playwright.chromium().launch(
                BrowserType.LaunchOptions().setHeadless(true).setTimeout(SEARCH_PAGE_TIMEOUT)
            )

            val page = browser.newPage(
                Browser.NewPageOptions().setUserAgent(PC_UA)
            )

            for (keyword in keywords) {
                for (pageNumber in 1..MAX_PAGE) {
                    page.navigate(searchUrl(keyword, pageNumber))
                    val articleCards = page.locator(articleCardsSelector).all()

                    for (articleCard in articleCards) {
                        val titleElement = articleCard.evaluate("el => el.querySelector('${articleTitleSelector}')") as Locator
                        val descriptionElement = articleCard.evaluate("el => el.querySelector('${articleDescriptionSelector}')") as Locator
                        val article = Article(
                            title = titleElement.innerText(),
                            description = descriptionElement.innerText(),
                            content = crawlArticle(titleElement.getAttribute("href"))
                        )

                        if (article.content.split(" ").size > MIN_WORDS) {
                            val articleJson = Json.encodeToString(Article.serializer(), article)
                            val file = Paths.get("files/articles/$keyword/$pageNumber.json").toFile()
                            FileOutputStream(file).use { outputStream ->
                                outputStream.write(articleJson.toByteArray())
                            }
                        }
                    }
                }
            }
            browser.close()
        }
    }

    private fun crawlArticle(url: String): String {
        var content = ""
        Playwright.create().use { playwright ->
            val browser = playwright.chromium().launch(
                BrowserType.LaunchOptions().setHeadless(true).setTimeout(ARTICLE_PAGE_TIMEOUT)
            )

            val page = browser.newPage(
                Browser.NewPageOptions().setUserAgent(MOBILE_UA)
            )

            page.navigate(url)
            for (selector in articleSelectors) {
                val elements = page.locator(selector).all()
                content = Html2MarkdownConverter.convert(elements)
                if (content.isNotEmpty()) {
                    break
                }
            }
            browser.close()
        }

        return content
    }

}