package com.sushistack.linkstacker.model.common.enums

enum class AuthorizationScheme(private val prefix: String) {
    BASIC("Basic"),
    BEARER("Bearer"),
    DIGEST("Digestest")
}