package com.sushistack.linktree.config.ftp

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "ftp")
data class FTPProperties(
    var host: String = "localhost",
    var port: Int = 21,
    var username: String = "",
    var password: String = ""
)