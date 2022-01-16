package com.rxplayer.rxplayer.handlers

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import java.nio.file.Paths

@Component
class FileHandler {

    @Value("\${rx-player.music-folder}")
    private lateinit var musicFolder: String

    @Value("\${rx-player.cover-folder}")
    private lateinit var coverFolder: String

    suspend fun getMusicFile(serverRequest: ServerRequest): ServerResponse {
        val filename = serverRequest.pathVariable("filename")
        val filepath = "${musicFolder}${filename}"
        return ServerResponse.ok()
            .bodyValueAndAwait(FileSystemResource(Paths.get(filepath)))
    }

    suspend fun getCoverImage(serverRequest: ServerRequest): ServerResponse {
        val filename = serverRequest.pathVariable("filename")
        val filepath = "${coverFolder}${filename}"
        return ServerResponse.ok()
            .bodyValueAndAwait(FileSystemResource(Paths.get(filepath)))
    }

}