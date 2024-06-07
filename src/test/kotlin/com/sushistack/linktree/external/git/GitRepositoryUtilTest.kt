package com.sushistack.linktree.external.git

import io.github.oshai.kotlinlogging.KotlinLogging
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class GitRepositoryUtilTest {

    private val log = KotlinLogging.logger {}

    @Value("\${spring.application.name}")
    private lateinit var appName: String

    @Value("\${test.bitbucket.username}")
    private lateinit var bitbucketUsername: String

    @Value("\${test.bitbucket.app-password}")
    private lateinit var bitbucketAppPassword: String

    private lateinit var homeDir: String

    @BeforeEach
    fun setup() {
        homeDir = "${System.getProperty("user.home")}/${appName}"
        log.info { "Path: $homeDir" }
    }

    @Test
    @Disabled("Need a appPassword of bitbucket repository")
    fun openTest() {
        // Given
        val workspaceName = bitbucketUsername
        val repositoryName = "playground"
        val appPassword = bitbucketAppPassword

        // When
        val git = GitRepositoryUtil.open(homeDir, workspaceName, repositoryName, appPassword)

        // Then
        Assertions.assertThat(git).isNotNull
    }
}