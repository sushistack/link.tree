package com.sushistack.linktree.jobs.link.gen.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class LinkProviderTest {
    private val log = KotlinLogging.logger {}

    @ParameterizedTest
    @MethodSource("anchorTextsProvider")
    fun getTest() {
        val provider = LinkProvider("https://test.com", listOf())

        repeat(20) {
            log.info { provider.get() }
        }

    }

    companion object {
        @JvmStatic
        fun anchorTextsProvider() = listOf(
            """["a", "b", "c", "d", "e", "f"]""",
        )
    }
}