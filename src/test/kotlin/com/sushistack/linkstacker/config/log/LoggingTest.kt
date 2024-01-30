package com.sushistack.linkstacker.config.log

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

private val log = KotlinLogging.logger {}

@SpringBootTest
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