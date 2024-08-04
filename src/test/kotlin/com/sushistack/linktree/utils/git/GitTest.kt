package com.sushistack.linktree.utils.git

import com.sushistack.linktree.utils.git.enums.ResetType
import io.github.oshai.kotlinlogging.KotlinLogging
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File

class GitTest {
    private val log = KotlinLogging.logger {}
    private lateinit var git: Git

    @BeforeEach
    fun setup() {
        val appHomeDir = "${System.getProperty("user.home")}/link.tree"
        val workspaceName = "linkswap"
        val repositoryName = "pbn-001"

        git = Git(appHomeDir, workspaceName, repositoryName)
    }

    @Test
    @DisplayName("repoDir should not be blank")
    fun repoDirNotBlank() {
        assertThat(git.repoDir).isNotBlank
    }

    @Test
    @DisplayName("status should return non-blank output")
    fun status() {
        val testFile = File(git.repoDir, "testFile.txt")
        testFile.writeText("Test content")

        val testFile2 = File(git.repoDir, "testFile2.txt")
        testFile2.writeText("Test content")

        git.add("testFile2.txt")
        val statusOutput = git.status()
        log.info { "status: $statusOutput" }

        git.reset()
        testFile.delete()
        testFile2.delete()
    }

    @Test
    @DisplayName("pull should execute without errors")
    fun pull() {
        val pullOutput = git.pull(Git.DEFAULT_BRANCH)
        assertThat(pullOutput).isNotBlank
    }

    @Test
    @DisplayName("pull with branch should execute without errors")
    fun pullWithBranch() {
        val pullOutput = git.pull(Git.DEFAULT_BRANCH)
        assertThat(pullOutput).isNotBlank
    }

    //@Test
    @DisplayName("push should execute without errors and reset after pushing")
    fun push() {
        git.checkout(Git.DEFAULT_BRANCH)
        // Make changes and commit them before pushing
        val testFile = File(git.repoDir, "testFile.txt")
        testFile.writeText("Test content")
        git.addAll()
        git.commit("Add test file")

        val pushOutput = git.push()
        assertThat(pushOutput).isNotBlank

        // Reset and force push
        git.reset(type = ResetType.HARD, hash = "HEAD~")
        val forcePushOutput = git.push(Git.DEFAULT_BRANCH, force = true)
        assertThat(forcePushOutput).isNotBlank
    }

    @Test
    @DisplayName("addAll should add changes to staging area and delete the file")
    fun addAll() {
        // Create a file and add it to the staging area
        val testFile = File(git.repoDir, "testFile.txt")
        testFile.writeText("Test content")
        git.addAll()
        // Check that the file is in the staging area
        val staged = git.status().staged
        log.info { staged }
        assertThat(staged).contains("testFile.txt")

        // Reset And Delete the file
        git.reset()
        testFile.delete()

        // Check that the file is no longer in the staging area
        val untracked = git.status().untracked
        log.info { untracked }
        assertThat(untracked).doesNotContain("testFile.txt")
    }

    @Test
    @DisplayName("commit should execute without errors")
    fun commit() {
        // Create a file
        val testFile = File(git.repoDir, "testFile.txt")
        testFile.writeText("Test content")

        // Add and commit the file
        git.addAll()
        val commitMessage = "Test commit"
        git.commit(commitMessage)

        // Check that the commit was made
        val commitId = git.getCommitId()
        assertThat(commitId).isNotBlank

        // Check that the file was committed
        val commited = git.commitedFiles(commitId)
        assertThat(commited).contains("testFile.txt")

        // Reset
        git.reset(type = ResetType.HARD, hash = "HEAD~")
    }

    @Test
    @DisplayName("checkout should execute without errors")
    fun checkout() {
        val checkoutOutput = git.checkout(Git.DEFAULT_BRANCH)
        assertThat(checkoutOutput).isNotBlank
    }

    @Test
    @DisplayName("branch should return non-blank output")
    fun branch() {
        val branchOutput = git.branch()
        assertThat(branchOutput).isNotBlank
    }

    @Test
    @DisplayName("branchExists should return true for existing branch")
    fun branchExistsTrue() {
        assertThat(git.branchExists(Git.DEFAULT_BRANCH)).isTrue
    }

    @Test
    @DisplayName("branchExists should return false for non-existing branch")
    fun branchExistsFalse() {
        assertThat(git.branchExists("nonExistingBranch")).isFalse
    }

    @Test
    @DisplayName("deleteBranch should execute without errors")
    fun createAndDeleteBranch() {
        val newBranch = "newBranch"
        val branchExists = git.branchExists(newBranch)
        log.info { "branch exists: $branchExists" }
        if (!branchExists) {
            val createBranchOutput = git.createBranch(newBranch)
            log.info { "branch : $createBranchOutput" }
            assertThat(createBranchOutput).isNotBlank()
            val checkoutOutput = git.checkout(Git.DEPLOY_BRANCH)
            log.info { "branch : $checkoutOutput" }
        }
        val deleteBranchOutput = git.deleteBranch(newBranch)
        assertThat(deleteBranchOutput).isNotBlank
    }

    @Test
    @DisplayName("reset should execute without errors")
    fun reset() {
        val commitId1 = git.getCommitId()
        // Create a file
        val testFile = File(git.repoDir, "testFile.txt")
        testFile.writeText("Test content")

        // Add and commit the file
        git.addAll()
        val commitMessage = "Test commit"
        git.commit(commitMessage)

        val commitId2 = git.getCommitId()
        assertThat(commitId2).isNotEqualTo(commitId1)

        git.reset(type = ResetType.HARD, hash = "HEAD~")

        val commitId3 = git.getCommitId()

        assertThat(commitId1).isEqualTo(commitId3)
    }

    @Test
    @DisplayName("getCommitId should return non-blank commit ID")
    fun getCommitId() {
        val commitId = git.getCommitId()
        assertThat(commitId).isNotBlank
    }
}