package com.sushistack.linktree.model

import com.sushistack.linktree.external.excel.config.ExcelColumn

data class LinkTable(
    @ExcelColumn(name = "타겟 URL", order = 1) val parentUrl: String,
    @ExcelColumn(name = "소스 URL", order = 2) val url: String,
    @ExcelColumn(name = "생성일", order = 3) val date: String
)