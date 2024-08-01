package com.sushistack.linktree.utils

fun ellipsis(files: List<String>): String {
    val first = files.firstOrNull() ?: return ""
    val count = files.size - 1
    return if (count > 0) "$first (외 ${count}개)" else first
}