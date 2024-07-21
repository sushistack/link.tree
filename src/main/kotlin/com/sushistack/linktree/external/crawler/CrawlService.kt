package com.sushistack.linktree.external.crawler

import com.microsoft.playwright.*
import com.microsoft.playwright.BrowserType.LaunchOptions
import com.sushistack.linktree.external.crawler.CrawlVariables.Companion.ARTICLE_PAGE_TIMEOUT
import com.sushistack.linktree.external.crawler.CrawlVariables.Companion.MAX_PAGE
import com.sushistack.linktree.external.crawler.CrawlVariables.Companion.MIN_WORDS
import com.sushistack.linktree.external.crawler.CrawlVariables.Companion.MOBILE_UA
import com.sushistack.linktree.external.crawler.CrawlVariables.Companion.PAGE_DEFAULT_TIMEOUT
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
import java.util.concurrent.atomic.AtomicInteger
import kotlin.io.path.exists

@Service
class CrawlService(private val appHomeDir: String) {
    private val log = KotlinLogging.logger {}
    private val articleCounter = AtomicInteger(0)

    fun crawl(keywords: List<String>) {
        val notCrawledKeywords = keywords.filter { keyword -> !Paths.get("${appHomeDir}/files/articles/$keyword").exists() }
        Playwright.create().use { playwright ->
            browser(playwright, LaunchOptions().setTimeout(SEARCH_PAGE_TIMEOUT))?.use { browser ->
                page(browser, PC_UA)?.use { page ->
                    articleCounter.set(0)
                    for (keyword in notCrawledKeywords) {
                        for (pageNumber in 1..MAX_PAGE) {
                            log.info { "keyword := [$keyword], page := [$pageNumber]" }
                            navigate(page, searchUrl(keyword, pageNumber)) ?: continue
                            val locators = selectorAll(page, articleCardsSelector)
                            collect(locators, keyword)
                        }
                    }
                }
            }
        }
    }

    fun collect(locators: List<Locator>, keyword: String) {
        for (locator in locators) {
            try {
                val titleLocator = selector(locator, articleTitleSelector) ?: continue
                val descLocator = selector(locator, articleDescriptionSelector) ?: continue

                val titleText = titleLocator.innerText()
                val descText = descLocator.innerText()
                val href = titleLocator.getAttribute("href") ?: continue

                val content = crawlContent(href)

                val article = Article(
                    title = titleText,
                    description = descText,
                    content = content
                )

                if (article.wordCount > MIN_WORDS) {
                    writeArticle(article, keyword)
                    log.info { "Write article successfully!!" }
                } else {
                    log.info { "Skip writing article." }
                }
            } catch (e: Exception) {
                log.error(e) { "Error processing locator: $locator" }
            }
        }
    }

    private fun handlePlayWrightException(e: Exception, message: String = "") = when(e) {
        is TimeoutError -> log.error { "$message (Timed out)" }
        else -> log.error(e) { message }
    }

    private fun browser(playwright: Playwright, launchOptions: LaunchOptions) =
        try {
            playwright.chromium().launch(launchOptions.setHeadless(true))
        } catch (e: PlaywrightException) {
            handlePlayWrightException(e, "Error during browser launch")
            null
        }

    private fun page(browser: Browser, userAgent: String) =
        try {
            browser.newPage(Browser.NewPageOptions().setUserAgent(userAgent))
                .apply { setDefaultTimeout(PAGE_DEFAULT_TIMEOUT) }
        } catch (e: PlaywrightException) {
            handlePlayWrightException(e, "Error during page creation")
            null
        }

    private fun navigate(page: Page, url: String): Response? =
        try {
            log.info { "searchUrl := $url" }
            page.navigate(url)
        } catch (e: PlaywrightException) {
            handlePlayWrightException(e, "Error during page navigation.")
            null
        }

    private fun selectorAll(page: Page, selector: String): List<Locator> =
        try {
            log.info { "locator all of page ($selector)" }
            page.locator(selector).all()
        } catch (e: PlaywrightException) {
            handlePlayWrightException(e, "Failed to read selector all of ($selector)")
            emptyList()
        }

    private fun selector(locator: Locator, selector: String) =
        try {
            log.info { "locator of locator ($selector)" }
            locator.locator(selector)
        } catch (e: PlaywrightException) {
            handlePlayWrightException(e, "Failed to read selector of $selector")
            null
        }

    private fun crawlContent(url: String): String {
        var content = ""
        try {
            Playwright.create().use { playwright ->
                browser(playwright, LaunchOptions().setTimeout(ARTICLE_PAGE_TIMEOUT))?.use { browser ->
                    page(browser, MOBILE_UA)?.use { page ->
                        navigate(page, url)
                        for (selector in articleSelectors) {
                            val elements = selectorAll(page, selector)
                            content = Html2MarkdownConverter.convert(elements)
                            if (content.isNotEmpty()) {
                                break
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            log.error(e) { "Error crawling content from URL: $url" }
        }
        return content
    }

    private fun writeArticle(article: Article, keyword: String) {
        val articleJson = Json.encodeToString(Article.serializer(), article)
        val file = Paths.get("${appHomeDir}/files/articles/$keyword/${articleCounter.getAndIncrement()}.json").toFile()
        file.parentFile?.let { parentDir ->
            if (!parentDir.exists()) {
                parentDir.mkdirs()
            }
        }
        FileOutputStream(file).use { outputStream ->
            outputStream.write(articleJson.toByteArray())
        }
    }
}