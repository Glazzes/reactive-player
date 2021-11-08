package com.rxplayer.rxplayer.requesthandlers

import com.rxplayer.rxplayer.service.PlayListService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class PlayListRequestHandler(private val playListService: PlayListService) {

    @Bean(name = ["playlist-co-router"])
    fun coroutineRouter() = coRouter {
        "/playlist".nest {
            POST("") { playListService.save(it) }
        }
    }

}