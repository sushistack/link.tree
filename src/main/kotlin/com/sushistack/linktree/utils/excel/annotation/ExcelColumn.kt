package com.sushistack.linktree.utils.excel.annotation

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExcelColumn(val name: String, val order: Int)
