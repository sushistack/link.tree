package com.sushistack.linktree.batch.config

enum class BatchJob(val jobName: String, val description: String) {
    CRAWL("crawlJob", "크롤링 작업"),
    LINK_GENERATION("linkGenerationJob", "링크 빌딩 작업"),
    POST_DEPLOY("postDeployJob", "게시물 배포 작업"),
    LINK_VALIDATION("linkValidationJob", "링크 유효성 검사 작업")
    ;
    companion object {
        fun getDescription(jobName: String): String =
            entries
                .find { it.jobName == jobName }
                ?.description
                ?: "알 수 없는 작업"
    }
}