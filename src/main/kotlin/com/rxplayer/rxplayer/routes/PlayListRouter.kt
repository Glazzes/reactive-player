package com.rxplayer.rxplayer.routes

import com.rxplayer.rxplayer.handlers.PlayListHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class PlayListRouter(private val playListHandler: PlayListHandler) {

    @Bean(name = ["playlist-co-router"])
    fun coroutineRouter() = coRouter {
        "/playlist".nest {
            POST("") { playListHandler.save(it) }
            GET("/{id}") { playListHandler.findById(it) }
            POST("/song") { playListHandler.addSong(it) }
            POST("/{id}/cover") { playListHandler.setCover(it) }
            GET("/{id}/songs") { playListHandler.findSongs(it) }
            DELETE("/song") { playListHandler.deleteSong(it) }
            DELETE("/{id}") { playListHandler.delete(it) }
        }
    }

}