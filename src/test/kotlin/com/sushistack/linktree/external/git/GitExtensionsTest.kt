package com.sushistack.linktree.external.git

import io.github.oshai.kotlinlogging.KotlinLogging
import org.assertj.core.api.Assertions
import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import java.io.File
import java.io.IOException

@SpringBootTest
class GitExtensionsTest {

    private val log = KotlinLogging.logger {}

    private lateinit var git: Git

    @Autowired
    private lateinit var appHomeDir: String

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

    }

    @Test
    fun gitPullTest() {
        // Given

        // When

        // Then
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

        // Then

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

        // Then
    }
}