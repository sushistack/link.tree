package com.sushistack.linktree.utils

import java.io.File
import java.io.IOException

@Throws(IOException::class)
fun File.moveRecursivelyTo(targetDir: File) {
    require(this.exists()) { "Source directory not exists: ${this.absolutePath}" }
    require(this.isDirectory) { "Source is not a directory: ${this.absolutePath}" }

    val success = this.copyRecursively(targetDir, overwrite = true)
    require(success) { "Failed to copy directory: ${this.absolutePath} -> ${targetDir.absolutePath}" }

    val deleted = this.deleteRecursively()
    require(deleted) { "Failed to delete directory: ${this.absolutePath}" }
}