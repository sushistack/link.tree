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
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import org.springframework.stereotype.Service
import java.io.FileOutputStream
import java.nio.file.Paths

@Service
class CrawlService(
    private val appHomeDir: String
) {

    private val log = KotlinLogging.logger {}

    fun crawlArticles(keywords: List<String>) {
        Playwright.create().use { playwright ->
            val browser = playwright.chromium().launch(
                BrowserType.LaunchOptions().setHeadless(true).setTimeout(SEARCH_PAGE_TIMEOUT)
            )

            val page = browser.newPage(
                Browser.NewPageOptions().setUserAgent(PC_UA)
            )

            var articleNumbers = 0
            for (keyword in keywords) {
                for (pageNumber in 1..MAX_PAGE) {
                    try {
                        page.navigate(searchUrl(keyword, pageNumber))
                    } catch (e: Exception) {
                        log.error(e) { "Page navigate failed!" }
                        continue
                    }
                    val articleCards = page.locator(articleCardsSelector).all()
                    log.info { "keyword := [$keyword], page := [${pageNumber}], article.size := [${articleCards.size}]" }
                    for (articleCard in articleCards) {
                        var titleElement: Locator
                        var descriptionElement: Locator
                        try {
                            titleElement = articleCard.locator(articleTitleSelector)
                            descriptionElement = articleCard.locator(articleDescriptionSelector)
                        } catch (e: Exception) {
                            log.error(e) { "Element locating failed!" }
                            continue
                        }
                        val article = Article(
                            title = titleElement.innerText(),
                            description = descriptionElement.innerText(),
                            content = crawlArticle(titleElement.getAttribute("href"))
                        )

                        if (article.content.split(" ").size > MIN_WORDS) {
                            val articleJson = Json.encodeToString(Article.serializer(), article)
                            val file = Paths.get("${appHomeDir}/files/articles/$keyword/${articleNumbers++}.json").toFile()
                            file.parentFile?.let { parentDir ->
                                if (!parentDir.exists()) {
                                    parentDir.mkdirs()
                                }
                            }
                            FileOutputStream(file).use { outputStream ->
                                outputStream.write(articleJson.toByteArray())
                            }
                            log.info { "Article is saved successfully!" }
                        } else {
                            log.info { "Skip saving Article." }
                        }
                    }
                }
            }
            browser.close()
        }
    }

    fun crawlArticle(url: String): String {
        var content = ""
        Playwright.create().use { playwright ->
            val browser = playwright.chromium().launch(
                BrowserType.LaunchOptions().setHeadless(true).setTimeout(ARTICLE_PAGE_TIMEOUT)
            )

            val page = browser.newPage(
                Browser.NewPageOptions().setUserAgent(MOBILE_UA)
            )

            try {
                page.navigate(url)
                for (selector in articleSelectors) {
                    val elements = page.locator(selector).all()
                    content = Html2MarkdownConverter.convert(elements)
                    if (content.isNotEmpty()) {
                        break
                    }
                }
            } catch (e: Exception) {
                log.error(e) { "crawl article failed! url := [$url]" }
            } finally {
                browser.close()
            }
        }

        return content
    }

}