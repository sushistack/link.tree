package com.sushistack.linktree.utils.git.enums

enum class ResetType(val cmdOpt: String) {
    SOFT("--soft"),
    MIXED("--mixed"),
    HARD("--hard");
}