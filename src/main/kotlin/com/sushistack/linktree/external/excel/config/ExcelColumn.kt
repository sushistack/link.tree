package com.sushistack.linktree.external.excel.config

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExcelColumn(val name: String, val order: Int)
