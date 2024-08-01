package com.sushistack.linktree.utils

import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

private val log = KotlinLogging.logger {}

@Throws(IOException::class)
fun File.moveRecursivelyTo(targetDir: File) {
    require(this.exists()) { "Source directory not exists: ${this.absolutePath}" }
    require(this.isDirectory) { "Source is not a directory: ${this.absolutePath}" }

    val success = this.copyRecursively(targetDir, overwrite = true)
    require(success) { "Failed to copy directory: ${this.absolutePath} -> ${targetDir.absolutePath}" }

    val deleted = this.deleteRecursively()
    require(deleted) { "Failed to delete directory: ${this.absolutePath}" }
}

fun Path.ls() =
    Files.list(this).use {
        it.forEach { path -> log.info { path } }
    }