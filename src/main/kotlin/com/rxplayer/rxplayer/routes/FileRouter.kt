package com.rxplayer.rxplayer.routes

import com.rxplayer.rxplayer.handlers.FileHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class FileRouter(
    private val fileHandler: FileHandler
){

    @Bean(name = ["file-coroutine-router"])
    fun coroutineRouter() = coRouter {
        "/files".nest {
            GET("/music/{filename}") { fileHandler.getMusicFile(it) }
            GET("/images/{filename}") { fileHandler.getCoverImage(it) }
        }
    }

}