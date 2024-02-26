package com.sushistack.linktree.config.log

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test

class LoggingTest {

    val log = KotlinLogging.logger {}

    @Test
    fun testLogging() {
        log.trace { "trace Test" }
        log.debug { "debug Test" }
        log.info { "info Test" }
        log.warn { "warn Test" }
        log.error { "error Test" }

    }
}