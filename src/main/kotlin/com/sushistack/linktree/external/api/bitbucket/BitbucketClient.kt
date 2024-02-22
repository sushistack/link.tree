package com.sushistack.linktree.external.api.bitbucket

import feign.Headers
import feign.Response
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(name = "bitbucketClient", url = "https://api.bitbucket.org/2.0/repositories")
interface BitbucketClient {

    @GetMapping("/{username}/{repository}/commits")
    fun getCommits(
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable("username") username: String,
        @PathVariable("repository") repository: String
    ): Response

    @PostMapping("/{username}/{repository}/src/")
    @Headers("Content-Type: application/json")
    fun addFileToRepository(
        @RequestHeader("Authorization") authHeader: String,
        @PathVariable("username") username: String,
        @PathVariable("repository") repository: String,
        @RequestBody formData: List<Pair<String, String>>
    ): Response

}