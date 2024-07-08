package com.sushistack.linktree.service

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier

@SpringBootTest
class ValidationServiceTest {

    @Autowired
    private lateinit var validationService: ValidationService

    @ParameterizedTest
    @MethodSource("urlsProvider")
    fun validatePostsTest(urls: List<String>) {
        // Given

        // When

        // Then
        val result = validationService.validatePosts(urls)

        StepVerifier.create(result)
            .expectNext(
                listOf(
                    UrlStatus("https://httpbin.org/status/200", 200),
                    UrlStatus("https://httpbin.org/status/201", 201),
                    UrlStatus("https://httpbin.org/status/301", 301),
                    UrlStatus("https://httpbin.org/status/304", 304),
                    UrlStatus("https://httpbin.org/status/400", 400),
                    UrlStatus("https://httpbin.org/status/401", 401),
                    UrlStatus("https://httpbin.org/status/404", 404),
                    UrlStatus("https://httpbin.org/status/429", 429),
                    UrlStatus("https://httpbin.org/status/500", 500),
                    UrlStatus("https://httpbin.org/status/503", 503)
                )
            ).verifyComplete()
    }

    companion object {
        @JvmStatic
        fun urlsProvider() = listOf(
            Arguments.of(
                listOf(
                    "https://httpbin.org/status/200",
                    "https://httpbin.org/status/201",
                    "https://httpbin.org/status/301",
                    "https://httpbin.org/status/304",
                    "https://httpbin.org/status/400",
                    "https://httpbin.org/status/401",
                    "https://httpbin.org/status/404",
                    "https://httpbin.org/status/429",
                    "https://httpbin.org/status/500",
                    "https://httpbin.org/status/503"
                )
            ),

        )
    }
}