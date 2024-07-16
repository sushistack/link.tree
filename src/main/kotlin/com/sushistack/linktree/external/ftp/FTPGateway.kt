package com.sushistack.linktree.external.ftp

import org.springframework.integration.annotation.Gateway
import org.springframework.integration.annotation.GatewayHeader
import org.springframework.integration.annotation.MessagingGateway
import org.springframework.messaging.handler.annotation.Header

@MessagingGateway
interface FTPGateway {
    @Gateway(
        requestChannel = "checkInboundChannel",
        replyChannel = "checkOutboundChannel",
        headers = [GatewayHeader(name = "remoteDir", expression = "args[0]")]
    )
    fun getFiles(remoteDir: String): List<String>

    @Gateway(
        requestChannel = "uploadChannel",
        headers = [GatewayHeader(name = "remoteDir", expression = "args[0]")]
    )
    fun uploadFile(
        @Header("remoteDir") remoteDir: String,
        @Header("fileName") fileName: String,
        file: ByteArray
    )

    @Gateway(
        requestChannel = "deleteChannel",
        headers = [
            GatewayHeader(name = "remoteDir", expression = "args[0]"),
            GatewayHeader(name = "fileName", expression = "args[1]")
        ]
    )
    fun deleteFile(@Header("remoteDir") remoteDir: String, fileName: String)
}