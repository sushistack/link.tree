package com.sushistack.linktree.external.crawler

class CrawlVariables {
    companion object {
        const val PC_UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36"
        const val MOBILE_UA = "Mozilla/5.0 (iPhone; CPU iPhone OS 12_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/71.0.3758.78 Mobile/15E148 Safari/605.1"
        const val SEARCH_PAGE_TIMEOUT = 4000.0
        const val ARTICLE_PAGE_TIMEOUT = 30000.0
        const val MAX_PAGE = 10
        const val MIN_WORDS = 300
        val headings = listOf("h1", "h2", "h3", "h4", "h5", "h6")
        val articleSelectors = listOf(".blogview_content", ".tt_article_useless_p_margin", ".jb-cell-content", "article", ".se-main-container")
                .map { "css=$it h1,$it h2,$it h3,$it h4,$it h5,$it h6,$it p:not(.og-title):not(.og-host):not(.og-desc)" }
        val searchUrl: (String, Int) -> String = { keyword, page -> "https://search.daum.net/search?w=fusion&col=blog&q=${keyword}&DA=TWA&p=${page}" }
        const val articleCardsSelector = "#twcColl .c-item-doc"
        const val articleTitleSelector = ".tit-g.clamp-g > a"
        const val articleDescriptionSelector = ".conts-desc.clamp-g > a"
    }
}