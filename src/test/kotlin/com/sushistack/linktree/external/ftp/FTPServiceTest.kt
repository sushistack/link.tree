package com.sushistack.linktree.external.ftp

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class FTPServiceTest {
    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var ftpGateway: FTPGateway

    @Autowired
    private lateinit var ftpService: FTPService

    @Test
    fun getFilesOnRemoteTest() = runBlocking {
        val files = ftpService.getFilesOnRemote("/public_html/acrid-caring.com/life")
        log.info { files }
    }

    @Test
    fun uploadTest() = runBlocking {
        ftpGateway.uploadFile("/public_html/acrid-caring.com/life", "test.txt", "abcd".toByteArray(Charsets.UTF_8))
        println("asdakdaskdaskdmaskdmakdmk")
    }

    @Test
    fun deleteTest() = runBlocking {
        ftpGateway.deleteFile("/public_html/acrid-caring.com/life", "text.txt")
    }
}