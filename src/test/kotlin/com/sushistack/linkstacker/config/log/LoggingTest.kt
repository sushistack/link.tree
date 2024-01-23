package com.sushistack.linkstacker.config.log

import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.springframework.boot.test.context.SpringBootTest

class LoggingAspectTest {
    @Log
    private lateinit var log: Logger

    @Test
    fun testLogging() {
        log.info("TEST")
    }
}