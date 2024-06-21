package com.sushistack.linktree.jobs.link.gen.service

class LinkProvider(private val targetUrl: String, private val anchorTexts: List<String>): java.io.Serializable {
    private var usageCounts: MutableMap<String, Int> = mutableMapOf()

    init {
        for (anchorText in anchorTexts) {
            usageCounts[anchorText] = 0
        }
    }

    fun get(): Pair<String, String> {
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
        return candidate to targetUrl
    }
}