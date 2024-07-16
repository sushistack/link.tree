package com.sushistack.linktree.external.ftp

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FTPServiceTest {
    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var ftpGateway: FTPGateway

    @Autowired
    private lateinit var ftpService: FTPService

    @Value("\${test.bitbucket.username}")
    private lateinit var bitbucketUsername: String


    @Test
    fun getFilesOnRemoteTest() {
        val files = ftpGateway.getFiles("/public_html/test.com/life")
        log.info { files }
    }

    @Test
    fun uploadTest() = runBlocking {
        ftpService.upload(bitbucketUsername, "pbn-001", "test.com")
    }

    @Test
    fun uploadFileTest() = runBlocking {
        ftpGateway.uploadFile("/public_html/test.com/life", "test.md", "abcd".toByteArray(Charsets.UTF_8))
    }

    @Test
    fun deleteTest() = runBlocking {
        ftpGateway.deleteFile("/public_html/test/life", "text.txt")
    }
}