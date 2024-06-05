package com.sushistack.linktree.external.git

import org.eclipse.jgit.api.Git
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

class GitExtensionsKtTest {

    private lateinit var git: Git

    @BeforeEach
    fun setup() {
        git = Git.open(File("test.git"))
    }

    @Test
    fun gitAddAndCommitTest() {
        // Given

        // When



        // Then
    }

}