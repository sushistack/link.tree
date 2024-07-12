package com.sushistack.linktree.service

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SlackNotificationServiceTest {

    @Autowired
    lateinit var slackNotificationService: SlackNotificationService

    @Test
    @Disabled
    fun sendTest() {
        slackNotificationService.send(message = "Hello, world!")
    }
}