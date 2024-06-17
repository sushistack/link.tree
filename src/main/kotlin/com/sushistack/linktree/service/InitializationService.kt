package com.sushistack.linktree.service

import com.sushistack.linktree.model.vo.CommentableWebpageVO
import com.sushistack.linktree.model.vo.GitAccountVO
import com.sushistack.linktree.model.vo.GitRepositoryVO
import com.sushistack.linktree.model.vo.StaticWebpageVO
import com.sushistack.linktree.repository.git.GitAccountRepository
import com.sushistack.linktree.repository.git.GitRepoRepository
import com.sushistack.linktree.repository.publisher.CommentableWebpageRepository
import com.sushistack.linktree.repository.publisher.StaticWebpageRepository
import com.sushistack.linktree.utils.CsvUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import org.jasypt.encryption.StringEncryptor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InitializationService(
    private val stringEncryptor: StringEncryptor,
    private val gitRepoRepository: GitRepoRepository,
    private val gitAccountRepository: GitAccountRepository,
    private val staticWebpageRepository: StaticWebpageRepository,
    private val commentableWebpageRepository: CommentableWebpageRepository
) {
    companion object {
        private const val ACCOUNT_FILE_PATH = "csv/account.csv"
        private const val REPOSITORY_FILE_PATH = "csv/repository.csv"
        private const val WEBPAGE_FILE_PATH = "csv/webpage.csv"
        private const val COMMENT_FILE_PATH = "csv/comment.csv"
    }

    private val log = KotlinLogging.logger {}

    @Transactional
    fun initialize() {
        val cc = commentableWebpageRepository.findAll()
        if (cc.isEmpty()) {
            val commentableWebpages = CsvUtils.read<CommentableWebpageVO>(COMMENT_FILE_PATH)
                .map { it.toEntity(stringEncryptor::decrypt) }
                .let { commentableWebpageRepository.saveAll(it) }

            log.info { "commentableWebpages.size = ${commentableWebpages.size}" }
        } else {
            log.info { "skip saving, commentableWebpages.size = ${cc.size}" }
        }

        val ac = gitAccountRepository.findAll()
        if (ac.isEmpty()) {
            val gitAccounts = CsvUtils.read<GitAccountVO>(ACCOUNT_FILE_PATH)
                .map { it.toEntity(stringEncryptor::decrypt) }
                .let { gitAccountRepository.saveAll(it) }
            log.info { "gitAccounts.size = ${gitAccounts.size}" }
        } else {
            log.info { "skip saving, gitAccounts.size = ${ac.size}" }
        }

        val sc = staticWebpageRepository.findAll()
        if (sc.isEmpty()) {
            val webpages = CsvUtils.read<StaticWebpageVO>(WEBPAGE_FILE_PATH)
                .map { it.toEntity(stringEncryptor::decrypt) }
                .let { staticWebpageRepository.saveAll(it) }

            log.info { "webpages.size = ${webpages.size}" }
        } else {
            log.info { "skip saving, webpages.size = ${sc.size}" }
        }

        val rc = gitRepoRepository.findAll()
        if (rc.isEmpty()) {
            val gitAccounts = gitAccountRepository.findAll()
            val webpages = staticWebpageRepository.findAll()
            val repositories = CsvUtils.read<GitRepositoryVO>(REPOSITORY_FILE_PATH).mapIndexed { index, repo ->
                val account = gitAccounts.find { ga -> ga.username == stringEncryptor.decrypt(repo.account) } ?: throw IllegalArgumentException("Not found")
                repo.toEntity(webpages[index], account, stringEncryptor::decrypt)
            }.let { gitRepoRepository.saveAll(it) }

            log.info { "repositories.count = ${repositories.size}" }
        } else {
            log.info { "skip saving, repositories.count = ${rc.size}" }
        }
    }

    @Transactional
    fun clearAll() {
        commentableWebpageRepository.deleteAll()
        gitRepoRepository.findAll().forEach {
            it.gitAccount = null
            it.webpage = null
        }
    }
}