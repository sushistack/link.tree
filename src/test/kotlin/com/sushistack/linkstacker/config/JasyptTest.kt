package com.sushistack.linkstacker.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jasypt.encryption.StringEncryptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

@SpringBootTest
class JasyptTest {

    private val log = KotlinLogging.logger {}

    @Autowired
    private lateinit var stringEncryptor: StringEncryptor

    @ParameterizedTest
    @ValueSource(strings = ["abcd", "1234", "!@#$"])
    fun encryptTest(plainText: String) {
        val encryptedText: String = stringEncryptor.encrypt(plainText)
        log.info { "Encrypted Text: $encryptedText" }

        val decryptedText: String = stringEncryptor.decrypt(encryptedText)
        log.info { "Decrypted Text: $decryptedText" }

        Assertions.assertThat(plainText).isEqualTo(decryptedText)
    }
}