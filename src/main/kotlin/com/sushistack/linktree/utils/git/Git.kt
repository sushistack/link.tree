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
        require(repoDir.isNotBlank()) { "repoDir is empty." }
        if (!Files.exists(Paths.get(repoDir))) {
            Files.createDirectories(Paths.get(repoDir))
        }

        val gitDir = File(repoDir, ".git")
        try {
            when (gitDir.exists()) {
                true -> {
                    if (this.branch() == DEPLOY_BRANCH) {
                        log.info { "Branch $DEPLOY_BRANCH to $DEFAULT_BRANCH" }
                        this.clean()
                        this.reset(type = ResetType.HARD, hash = "HEAD")
                        this.checkout(DEFAULT_BRANCH)
                    }
                    /* this.pull(DEFAULT_BRANCH) */
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

    fun status(): FileStatus =
        command("status", "--short").lines()
            .filter { it.isNotBlank() }
            .let { lines ->
                FileStatus(
                    untracked = lines.filter { it.substring(0, 3).trim() == "??" }.map { it.substring(3) },
                    staged = lines.filter { listOf("A", "C", "AD").contains(it.substring(0, 3).trim()) }.map { it.substring(3) },
                    changed = lines.filter { listOf("M", "D", "R", "AM", "MA").contains(it.substring(0, 3).trim()) }.map { it.substring(3) }
                )
        }

    fun pull(branch: String): String = command("pull", "origin", branch)

    fun push(branch: String, force: Boolean = false): String {
        if (force) {
            return command("push", "origin", branch, "-f")
        }
        val res = command("push", "origin", branch)
        if ("(fetch first)" in res) {
            log.info { "Conflict with Origin, Pull and Push" }
            this.pull(branch)
        }
        return command("push", "origin", branch)
    }


    fun add(path: String): String = command("add", path)

    fun addAll(): String = command("add", ".")

    fun commit(message: String): String = command("commit", "-m", message)

    fun commitedFiles(hash: String): List<String> =
        command("show", "--name-only", "--pretty=format:", hash)
            .lines()
            .filter { it.isNotBlank() }

    fun checkout(branch: String): String = command("checkout", branch)
        .also { log.info { it } }

    fun branch(): String = command("branch")

    fun branchExists(branch: String): Boolean =
        command("branch", "--list", branch)
            .split("\n")
            .any { it.trim() == branch || it.trim().removePrefix("* ") == branch }

    fun createBranch(branch: String): String =
        command("checkout", "-b", branch)
            .also { log.info { it } }

    fun deleteBranch(branch: String): String =
        command("branch", "-D", branch)
            .also { log.info { it } }

    fun reset(type: ResetType = ResetType.MIXED, hash: String = "HEAD"): String =
        command("reset", type.cmdOpt, hash)

    fun clean(): String = command("clean", "-fd")

    fun getCommitId(): String =
        command("log", "-n", "1", "--oneline")
            .split(" ")[0]

    data class FileStatus(
        val untracked: List<String>,
        val staged: List<String>,
        val changed: List<String>
    )
}