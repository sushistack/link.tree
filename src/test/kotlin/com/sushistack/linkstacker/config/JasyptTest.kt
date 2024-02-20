package com.sushistack.linkstacker.config

import com.sushistack.linkstacker.log
import org.assertj.core.api.Assertions
import org.jasypt.encryption.StringEncryptor
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class JasyptTest {

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