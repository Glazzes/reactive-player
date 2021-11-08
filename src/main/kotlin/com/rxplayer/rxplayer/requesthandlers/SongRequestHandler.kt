package com.rxplayer.rxplayer.requesthandlers

import com.rxplayer.rxplayer.service.SongService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.router

@Configuration
class SongRequestHandler(private val songService: SongService){

    @Bean(name = ["song-co-router"])
    fun coroutineRouter() = coRouter {
        "/song".nest {
            POST("") { songService.save(it) }
            DELETE("/{id}") { songService.deleteById(it) }
        }
    }

    @Bean(name = ["song-flux-router"])
    fun webFluxRouter() = router {
        "/song".nest {
            GET("/{id}") { songService.findById(it) }
        }
    }

}