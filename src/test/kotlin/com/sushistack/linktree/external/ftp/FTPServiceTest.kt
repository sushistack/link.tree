package com.sushistack.linktree.external.ftp

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.net.URLEncoder

@ActiveProfiles("prod")
@SpringBootTest
class FTPServiceTest {
    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var ftpGateway: FTPGateway


    @Test
    fun getFilesOnRemoteTest() {
        val files = ftpGateway.getFiles("/public_html/test.com/life")
        log.info { files }
    }

    @Test
    fun uploadFileTest() = runBlocking {
        ftpGateway.uploadFile("/public_html/breakfast-rat.link/life", "참새.html", "abcd".toByteArray(Charsets.UTF_8))
    }

    @Test
    fun deleteTest() = runBlocking {
        ftpGateway.deleteFile("/public_html/test/life", "text.txt")
    }
}