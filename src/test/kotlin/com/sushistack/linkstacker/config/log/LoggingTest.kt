package com.sushistack.linkstacker.config.log

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test

private val log = KotlinLogging.logger {}

class LoggingTest {

    @Test
    fun testLogging() {
        log.info { "info Test" }
    }
}