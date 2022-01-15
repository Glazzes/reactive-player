package com.rxplayer.rxplayer.routes

import com.rxplayer.rxplayer.handlers.AuthHandler
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.coRouter

@Component
class AuthRouter(private val authHandler: AuthHandler){

    @Bean(name = ["auth-coroutine-router"])
    fun coroutineRouter() = coRouter {
        POST("/login") { authHandler.login(it) }
        POST("/logout") { authHandler.logout(it) }
    }

}