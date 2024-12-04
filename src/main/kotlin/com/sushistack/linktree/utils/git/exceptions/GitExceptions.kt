package com.sushistack.linktree.utils.git.exceptions


open class GitException(message: String) : RuntimeException(message)

class GitRejectedException(message: String) : GitException(message)
class GitNonFastForwardException(message: String) : GitException(message)
class GitAuthException(message: String) : GitException(message)
class GitMergeConflictException(message: String) : GitException(message)
class GitTimeoutException(message: String) : GitException(message)
class GitUnknownException(message: String) : GitException(message)

fun handleGitException(errorMessage: String): GitException {
    val isPushRejected = "! [rejected]" in errorMessage && "(fetch first)" in errorMessage
    val isNonFastForward = "! [rejected]" in errorMessage && "(non-fast-forward)" in errorMessage
    val isAuthError = "Authentication failed" in errorMessage || "fatal: could not read Username" in errorMessage
    val isMergeConflict = "CONFLICT (content): Merge conflict" in errorMessage || "Automatic merge failed" in errorMessage

    return when {
        isPushRejected -> GitRejectedException("Push rejected: Remote contains changes. Run `git pull` first.")
        isNonFastForward -> GitNonFastForwardException("Push failed due to non-fast-forward. Run `git pull --rebase` and try again.")
        isAuthError -> GitAuthException("Git authentication failed. Check your credentials.")
        isMergeConflict -> GitMergeConflictException("Merge conflict detected. Resolve conflicts and commit manually.")
        else -> GitUnknownException("Unknown Git error: $errorMessage")
    }
}