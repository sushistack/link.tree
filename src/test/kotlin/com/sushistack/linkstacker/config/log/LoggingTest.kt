package com.sushistack.linkstacker.config.log

import com.sushistack.linkstacker.log
import org.junit.jupiter.api.Test

class LoggingTest {

    @Test
    fun testLogging() {
        log.trace { "trace Test" }
        log.debug { "debug Test" }
        log.info { "info Test" }
        log.warn { "warn Test" }
        log.error { "error Test" }

    }
}