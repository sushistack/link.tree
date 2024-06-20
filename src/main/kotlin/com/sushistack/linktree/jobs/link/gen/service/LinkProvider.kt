package com.sushistack.linktree.jobs.link.gen.service

import kotlinx.serialization.json.Json
import org.springframework.batch.core.configuration.annotation.JobScope
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@JobScope
@Component
class LinkProvider(
    @Value("#{jobParameters['targetUrl']}") private val targetUrl: String,
    @Value("#{jobParameters['anchorTexts']}") private val anchorTextsJson: String
) {
    private final val url: String by lazy { targetUrl }
    private final val anchorTexts: List<String> by lazy { Json.decodeFromString<List<String>>(anchorTextsJson) }
    private final var usageCounts: MutableMap<String, Int> = mutableMapOf()

    init {
        for (anchorText in anchorTexts) {
            usageCounts[anchorText] = 0
        }
    }

    fun getMarkdownLink(): String {
        var minCount = Int.MAX_VALUE
        var candidate = ""
        for (anchorText in anchorTexts) {
            val count = usageCounts[anchorText] ?: Int.MAX_VALUE
            if (count < minCount) {
                minCount = count
                candidate = anchorText
            }
        }

        usageCounts[candidate] = (usageCounts[candidate] ?: 0) + 1
        return " [$candidate]($url) "
    }
}