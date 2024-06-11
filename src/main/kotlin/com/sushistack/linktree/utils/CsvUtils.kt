package com.sushistack.linktree.utils

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.decodeFromString
import org.springframework.core.io.ClassPathResource

@OptIn(ExperimentalSerializationApi::class)
object CsvUtils {
    val csv = Csv { hasHeaderRecord = true }

    inline fun <reified T> read(filePath: String): List<T> {
        val file = ClassPathResource(filePath)
        require(file.exists()) { "File does not exist: $filePath" }

        return csv.decodeFromString(file.inputStream.bufferedReader().readText())
    }

    inline fun <reified T> readText(input: String): List<T> {
        return csv.decodeFromString(input)
    }
}