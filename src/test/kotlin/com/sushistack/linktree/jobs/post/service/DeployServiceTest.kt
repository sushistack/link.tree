package com.sushistack.linktree.jobs.post.service

import com.sushistack.linktree.external.ftp.FTPService
import com.sushistack.linktree.jobs.post.service.DeployService.SimpleGitRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
class DeployServiceTest {

    private lateinit var deployService: DeployService

    @MockBean
    private lateinit var ftpService: FTPService

    @Value("\${test.bitbucket.username}")
    private lateinit var bitbucketUsername: String

    @Value("\${test.bitbucket.app-password}")
    private lateinit var bitbucketAppPassword: String

    @BeforeEach
    fun setup() {
        deployService = DeployService("/Users/nhn/link.tree", ftpService)
    }

    @Test
    fun makePackageTest() {
        runBlocking {
            deployService.makePackage(SimpleGitRepository(bitbucketUsername, "pbn-001", bitbucketUsername, bitbucketAppPassword))
        }

    }
}