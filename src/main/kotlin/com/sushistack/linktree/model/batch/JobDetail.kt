package com.sushistack.linktree.model.batch

data class JobDetail(
    val name: String,
    val status: String,
    val startTime: String,
    val endTime: String,
    val message: String? = null
)