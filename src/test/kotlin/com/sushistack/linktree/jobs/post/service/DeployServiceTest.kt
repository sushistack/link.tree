package com.sushistack.linktree.jobs.post.service

import com.sushistack.linktree.external.ftp.FTPService
import com.sushistack.linktree.jobs.post.service.DeployService.SimpleGitRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
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
    @Disabled
    fun makePackageTest() {
        runBlocking {
            deployService.makePackage(SimpleGitRepository(bitbucketUsername, "pbn-001", bitbucketUsername, bitbucketAppPassword))
        }

    }

    @Disabled
    @ParameterizedTest
    @MethodSource("pbnProvider")
    fun changeConfigYamlTest(repoName: String) {
//        runBlocking {
//            deployService.deploy(ServiceProviderType.CLOUD_BLOG_NETWORK, SimpleGitRepository(bitbucketUsername, repoName, bitbucketUsername, bitbucketAppPassword))
//        }
    }

    companion object {
        @JvmStatic
        fun pbnProvider() = (1..60).map {
            String.format("pbn-live%03d", it)
        }
    }
}