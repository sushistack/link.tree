package com.sushistack.linktree.external.llm.impl

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CohereServiceTest {
    private val log = KotlinLogging.logger {}

    @Autowired
    private var cohereService: CohereService? = null


    @Test
    @Disabled
    fun callTest() {
        // Given

        // When

        // Then
        val query = "코루틴에 대해서 설명해줘"
        val resText = cohereService?.call(query) ?: "Failed"
        log.info { "res: $resText" }
    }
}