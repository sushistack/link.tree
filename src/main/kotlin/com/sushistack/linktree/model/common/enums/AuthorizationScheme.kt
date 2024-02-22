package com.sushistack.linktree.model.common.enums

enum class AuthorizationScheme(val prefix: String) {
    BASIC("Basic"),
    BEARER("Bearer"),
    DIGEST("Digestest")
}