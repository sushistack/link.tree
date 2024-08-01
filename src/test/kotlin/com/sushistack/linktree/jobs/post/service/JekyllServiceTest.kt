package com.sushistack.linktree.jobs.post.service

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class JekyllServiceTest {

    private lateinit var jekyllService: JekyllService

    @Value("\${test.bitbucket.username}")
    private lateinit var bitbucketUsername: String

    @Value("\${test.bitbucket.app-password}")
    private lateinit var bitbucketAppPassword: String

    @BeforeEach
    fun setup() {
        jekyllService = JekyllService("/Users/nhn/link.tree")
    }
}