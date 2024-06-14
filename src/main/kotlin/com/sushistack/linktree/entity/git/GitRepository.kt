package com.sushistack.linktree.entity.git

import com.sushistack.linktree.entity.BaseTimeEntity
import com.sushistack.linktree.entity.publisher.StaticWebpage
import jakarta.persistence.*

@Entity
@Table(name = "lt_git_repository")
class GitRepository (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repository_seq", nullable = false)
    val repositorySeq: Long = 0,

    @Column(name = "workspace_name", nullable = false)
    val workspaceName: String = "",

    @Column(name = "repository_name", nullable = false)
    val repositoryName: String = "",

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "webpage_seq")
    var webpage: StaticWebpage? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "git_account_seq")
    val gitAccount: GitAccount? = null
): BaseTimeEntity() {
    fun changeWebPage(webpage: StaticWebpage) {
        this.webpage = webpage
        this.webpage?.repository = this
    }

    override fun toString(): String = "GitRepository(repositorySeq=$repositorySeq, workspaceName='$workspaceName', repositoryName='$repositoryName', webpage=$webpage, gitAccount=$gitAccount)"
}