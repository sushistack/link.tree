package com.sushistack.linktree.service.llm

interface LargeLanguageModelService {
    fun call(query: String): String
}