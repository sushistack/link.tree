package com.sushistack.linktree.external.excel.model

data class ExcelSheet<T> (
    val name: String,
    val rows: List<T>
)