package com.sushistack.linktree.service

import com.sushistack.linktree.external.git.GitRepositoryUtil
import org.eclipse.jgit.api.Git
import org.springframework.stereotype.Component

@Component
class GitFactory {

    fun openRepo(appHomeDir: String, workspaceName: String, repositoryName: String, appPassword: String): Git =
        GitRepositoryUtil.open(appHomeDir, workspaceName, repositoryName, appPassword)

}