package com.rxplayer.rxplayer.routes

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class HomeRouter {

    @Bean(name = ["home-coroutine-router"])
    fun coroutineRouter() = coRouter {
        GET("/") {
            ServerResponse.ok().bodyValueAndAwait("Welcome to RX Player, a reactive player made by Glaze <3")
        }
    }

}