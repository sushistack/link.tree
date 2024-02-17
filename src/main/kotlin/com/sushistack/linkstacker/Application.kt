package com.sushistack.linkstacker

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

val log = KotlinLogging.logger {}

@EnableFeignClients
@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
