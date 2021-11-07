package com.rxplayer.rxplayer.requesthandlers

import com.rxplayer.rxplayer.service.SongService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter


@Configuration
class SongRequestHandler(private val songService: SongService){

    @Bean(name = ["song-co-router"])
    fun coroutineRouter() = coRouter {
        "/song".nest {
            POST("") { songService.save(it) }
            GET("/{id}") { songService.findById(it) }
            DELETE("/{id}") { songService.deleteById(it) }
        }
    }

}