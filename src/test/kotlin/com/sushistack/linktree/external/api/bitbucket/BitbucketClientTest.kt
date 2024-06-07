package com.sushistack.linktree.external.api.bitbucket

import com.sushistack.linktree.entity.git.GitAccount
import com.sushistack.linktree.entity.git.GitRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus

@SpringBootTest
class BitbucketClientTest {

    @Autowired
    private lateinit var bitbucketClient: BitbucketClient
    private lateinit var gitAccount: GitAccount
    private lateinit var gitRepository: GitRepository

    @Value("\${test.bitbucket.username}")
    private lateinit var bitbucketUsername: String

    @Value("\${test.bitbucket.app-password}")
    private lateinit var bitbucketAppPassword: String

    @BeforeEach
    fun setup() {
        gitAccount = GitAccount(
            username = this.bitbucketUsername,
            appPassword = this.bitbucketAppPassword
        )
        gitRepository = GitRepository(repositoryName = "playground")
    }

    @Test
    fun getCommitsTest() {
        val res = bitbucketClient.getCommits(
            gitAccount.getAuthorization(),
            gitAccount.username,
            gitRepository.repositoryName
        )

        Assertions.assertThat(res.status()).isEqualTo(HttpStatus.OK.value())
    }

}