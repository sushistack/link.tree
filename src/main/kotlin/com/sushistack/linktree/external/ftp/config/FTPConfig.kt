package com.sushistack.linktree.external.ftp.config

import org.apache.commons.net.ftp.FTPClient.BINARY_FILE_TYPE
import org.apache.commons.net.ftp.FTPClient.PASSIVE_LOCAL_DATA_CONNECTION_MODE
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory

@Configuration
class FTPConfig(private val ftp: FTPProperties) {

    @Bean
    fun ftpSessionFactory(): DefaultFtpSessionFactory =
        DefaultFtpSessionFactory().apply {
            setHost(ftp.host)
            setPort(ftp.port)
            setUsername(ftp.username)
            setPassword(ftp.password)
            setClientMode(PASSIVE_LOCAL_DATA_CONNECTION_MODE)
            setFileType(BINARY_FILE_TYPE)
        }
}