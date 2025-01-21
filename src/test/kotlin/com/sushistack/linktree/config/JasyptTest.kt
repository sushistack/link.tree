package com.sushistack.linktree.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.assertj.core.api.Assertions
import org.jasypt.encryption.StringEncryptor
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class JasyptTest {

    val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var stringEncryptor: StringEncryptor

    @ParameterizedTest
    @ValueSource(strings = ["C08GWFW2N58", "1234", "!@#$"])
    fun encryptTest(plainText: String) {
        val encryptedText: String = stringEncryptor.encrypt(plainText)
        log.info { "Encrypted Text: $encryptedText" }

        val decryptedText: String = stringEncryptor.decrypt(encryptedText)
        log.info { "Decrypted Text: $decryptedText" }

        Assertions.assertThat(plainText).isEqualTo(decryptedText)
    }

    @Test
    fun decryptTest() {
        val decryptedText: String = stringEncryptor.decrypt("toIloszkclAJtHNqQsGXE8hxh2btyThe")
        log.info { "Decrypted Text: $decryptedText" }
    }
}