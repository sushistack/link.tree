package com.sushistack.linktree.external.llm

interface LargeLanguageModelService {
    fun call(query: String): String
}