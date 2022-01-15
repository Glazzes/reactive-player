package com.rxplayer.rxplayer.routes

import com.rxplayer.rxplayer.handlers.SongHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class SongRouter(private val songHandler: SongHandler){

    @Bean(name = ["song-co-router"])
    fun coroutineRouter() = coRouter {
        "/song".nest {
            POST("") { songHandler.save(it) }
            GET("/{id}") { songHandler.findById(it) }
            DELETE("/{id}") { songHandler.delete(it) }
        }
    }

}