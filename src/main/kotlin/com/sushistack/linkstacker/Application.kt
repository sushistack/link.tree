package com.sushistack.linkstacker

import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableFeignClients
@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
