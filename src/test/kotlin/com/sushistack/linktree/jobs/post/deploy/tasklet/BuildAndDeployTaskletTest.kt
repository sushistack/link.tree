package com.sushistack.linktree.jobs.post.deploy.tasklet

import com.sushistack.linktree.entity.publisher.ServiceProviderType.CLOUD_BLOG_NETWORK
import com.sushistack.linktree.jobs.post.service.DeployService
import com.sushistack.linktree.jobs.post.service.DeployService.SimpleGitRepository
import com.sushistack.linktree.jobs.post.service.JekyllService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BuildAndDeployTaskletTest {

    @Autowired
    private lateinit var jekyllService: JekyllService

    @Autowired
    private lateinit var deployService: DeployService

    @Value("\${test.bitbucket.username}")
    private lateinit var bitbucketUsername: String

    @Value("\${test.bitbucket.app-password}")
    private lateinit var bitbucketAppPassword: String

    @Test
    fun buildAndDeployTest() {
        jekyllService.build(bitbucketUsername, "pbn-003", bitbucketAppPassword)
        val repo = SimpleGitRepository(bitbucketUsername, "pbn-003", "test3.com", bitbucketUsername, bitbucketAppPassword)
        deployService.makePackage(repo)
        deployService.deploy(CLOUD_BLOG_NETWORK, repo)
    }

}