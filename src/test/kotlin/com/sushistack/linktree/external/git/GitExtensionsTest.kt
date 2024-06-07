package com.sushistack.linktree.external.git

import io.github.oshai.kotlinlogging.KotlinLogging
import org.assertj.core.api.Assertions
import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.io.IOException

@SpringBootTest
class GitExtensionsTest {

    private val log = KotlinLogging.logger {}

    private lateinit var git: Git

    @Value("\${spring.application.name}")
    private lateinit var appName: String

    @Value("\${test.bitbucket.username}")
    private lateinit var bitbucketUsername: String

    @Value("\${test.bitbucket.app-password}")
    private lateinit var bitbucketAppPassword: String

    private lateinit var homeDir: String

    private lateinit var repositoryName: String

    @BeforeEach
    fun setup() {
        homeDir = "${System.getProperty("user.home")}/${appName}"
        repositoryName = "playground"
        git = GitRepositoryUtil.open(
            appHomeDir = homeDir,
            workspaceName = bitbucketUsername,
            repositoryName = "playground",
            appPassword = bitbucketAppPassword
        )
    }

    @Test
    fun gitPullTest() {
        // Given

        // When
        val pullRes = git.pullChanges(username = bitbucketUsername, appPassword = bitbucketAppPassword)

        // Then
        Assertions.assertThat(pullRes).isNotNull
    }

    @Test
    fun gitAddAndCommitTest() {
        // Given
        val dir = "${homeDir}/repo/${bitbucketUsername}/${repositoryName}"
        val filePath = "${dir}/files.txt"
        log.info { filePath }
        val file = File(filePath)
        try {
            file.createNewFile()
            file.writeText("test")
        } catch (e: IOException) {
            log.info { e.message }
        }

        // When
        val commitRes = git.addAndCommit()

        // Then
        Assertions.assertThat(commitRes).isNotNull
        // git.resetTo()
    }

    @Test
    @Disabled("Because of Reset is risky")
    fun gitResetTest() {
        // Given

        // When
        // git.resetTo()

        // Then
        // Assertions.assertThat().isNotNull
    }

    @Test
    fun gitPushTest() {
        // Given

        // When
        val pushRes = git.push(username = bitbucketUsername, appPassword = bitbucketAppPassword)

        // Then
        pushRes.forEach { res ->
            Assertions.assertThat(res).isNotNull
        }

    }
}