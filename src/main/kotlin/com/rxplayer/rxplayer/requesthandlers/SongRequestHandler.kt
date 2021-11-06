package com.rxplayer.rxplayer.requesthandlers

import com.rxplayer.rxplayer.service.SongService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates.*
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions.*
import org.springframework.web.reactive.function.server.ServerResponse


@Configuration
class SongRequestHandler(private val songService: SongService){

    @Bean
    fun greet(): RouterFunction<ServerResponse> {
        return route(GET("/hello/{name}")){
            val name = it.pathVariable("name")
            songService.sayHello(name)
        }
    }

}