package com.sushistack.linkstacker.external.excel.model

data class ExcelSheet<T> (
    val name: String,
    val rows: List<T>
)