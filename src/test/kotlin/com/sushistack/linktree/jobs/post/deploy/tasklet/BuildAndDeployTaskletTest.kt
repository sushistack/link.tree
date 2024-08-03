package com.sushistack.linktree.jobs.post.deploy.tasklet

import com.sushistack.linktree.entity.publisher.ServiceProviderType.CLOUD_BLOG_NETWORK
import com.sushistack.linktree.jobs.post.service.DeployService
import com.sushistack.linktree.jobs.post.service.JekyllService
import com.sushistack.linktree.utils.git.Git
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

    @Autowired
    private lateinit var appHomeDir: String

    @Value("\${test.bitbucket.username}")
    private lateinit var bitbucketUsername: String

    @Value("\${test.bitbucket.app-password}")
    private lateinit var bitbucketAppPassword: String

    @Test
    fun buildAndDeployTest() {
        val git = Git(appHomeDir, bitbucketUsername, "pbn-003")
        jekyllService.build(git)
        deployService.makePackage(git)
        deployService.deploy(CLOUD_BLOG_NETWORK, git, "test.com")
    }

}