package com.sushistack.linktree.external.crawler

import com.microsoft.playwright.Locator
import com.sushistack.linktree.external.crawler.CrawlVariables.Companion.headings

class Html2MarkdownConverter {
    companion object {

        fun convert(elements: List<Locator>): String =
            elements.joinToString("") { el ->
                val tagName = (el.evaluate("el => el.tagName") as String).lowercase()
                "${head2Hash(tagName, getMinHeadNumber(elements))} ${el.innerText()}\n"
            }

        private fun head2Hash(tagName: String, minHeadNumber: Int) : String {
            if (headings.contains(tagName)) {
                val headNumber = tagName.removePrefix("h").toInt()
                return if (headNumber >= minHeadNumber) {
                    "\n" + "#".repeat( headNumber - minHeadNumber + 2)
                } else {
                    ""
                }
            }
            return ""
        }

        private fun getMinHeadNumber(elements: List<Locator>): Int =
            elements.map { el -> (el.evaluate("el => el.tagName") as String).lowercase() }
                .filter { tagName -> headings.contains(tagName) }
                .minOfOrNull { tagName -> tagName[1].toString().toInt() } ?: 7
    }
}