package com.sushistack.linktree.batch.config

enum class BatchJob(val jobName: String, val description: String, val idemponent: Boolean) {
    CRAWL("crawlJob", "크롤링 작업", true),
    POST_GENERATION("postGenerationJob", "기사 생성 작업", true),
    LINK_GENERATION("linkGenerationJob", "링크 빌딩 작업", false),
    POST_DEPLOY("postDeployJob", "게시물 배포 작업", true),
    LINK_VALIDATION("linkValidationJob", "링크 유효성 검사 작업", true),
    LINK_DELETION("linkDeletionJob", "링크 제거 작업", true);
    companion object {
        fun getDescription(jobName: String): String =
            entries
                .find { it.jobName == jobName }
                ?.description
                ?: "알 수 없는 작업"

        fun getIdemponentJobs(): List<String> =
            entries
                .filter { it.idemponent }
                .map { it.jobName }
    }
}