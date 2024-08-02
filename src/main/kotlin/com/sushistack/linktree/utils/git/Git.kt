package com.sushistack.linktree.utils.git

import com.sushistack.linktree.utils.git.enums.ResetType
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

class Git(
    val appHomeDir: String,
    val workspaceName: String,
    val repositoryName: String
) {
    companion object {
        const val DEFAULT_BRANCH = "gh-pages"
        const val DEPLOY_BRANCH = "master"
    }

    private val log = KotlinLogging.logger {}
    val repoDir: String by lazy { "$appHomeDir/repo/${workspaceName}/${repositoryName}" }
    private val sshUrl: String by lazy { "git@bitbucket.org:${workspaceName}/${repositoryName}.git" }

    init {
        require(repoDir.isNotBlank() && Files.exists(Paths.get(repoDir))) { "$repoDir does not exist." }
        val gitDir = File(repoDir, ".git")
        try {
            when (gitDir.exists()) {
                true -> {
                    this.clean()
                    this.checkout(DEFAULT_BRANCH)
                    this.pull(DEFAULT_BRANCH)
                }
                false -> this.clone()
            }
        } catch (e: GitException) {
            throw GitException("Can not pull or clone, ${e.message}")
        }
    }

    private fun command(vararg args: String, timeoutSeconds: Long = 10): String {
        return try {
            val process = ProcessBuilder("git", *args)
                .directory(File(repoDir))
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().use { it.readText() }
            if (!process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
                process.destroy()
                throw GitException("Git command timed out: git ${args.joinToString(" ")}")
            }

            if (process.exitValue() != 0) {
                throw GitException("Git command failed: git ${args.joinToString(" ")}\n$output")
            }

            output.trim()
        } catch (e: IOException) {
            throw GitException("Git command failed due to IO error: ${e.message}")
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw GitException("Git command interrupted: ${e.message}")
        }
    }
    private fun clone() = command("clone", sshUrl, repoDir)

    fun status(): String = command("status")

    fun pull(): String = command("pull")

    fun pull(branch: String): String = command("pull", "origin", branch)

    fun push(force: Boolean = false): String = command("push")

    fun push(branch: String, force: Boolean = false): String = command("push", "origin", branch)

    fun addAll(): String = command("add", ".")

    fun commit(message: String): String = command("commit", "-m", message)

    fun checkout(branch: String): String = command("checkout", branch)

    fun branch(): String = command("branch")

    fun branchExists(branch: String): Boolean =
        command("branch")
            .split("\n")
            .any { it.trim() == branch }

    fun createBranch(branch: String): String = command("checkout", "-b", branch)

    fun deleteBranch(branch: String): String = command("branch", "-D", branch)

    fun reset(type: ResetType = ResetType.HARD, hash: String = "HEAD~1"): String =
        command("reset", type.cmdOpt, hash)

    fun clean(): String = command("clean", "-fd")

    fun getCommitId(): String =
        command("git", "log", "-n", "1", "--oneline")
            .split(" ")[0]
}