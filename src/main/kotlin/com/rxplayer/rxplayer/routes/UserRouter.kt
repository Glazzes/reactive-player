package com.rxplayer.rxplayer.routes

import com.rxplayer.rxplayer.handlers.AuthHandler
import com.rxplayer.rxplayer.handlers.UserHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class UserRouter(
    private val userHandler: UserHandler,
    private val authHandler: AuthHandler
    ){

    @Bean(name = ["user-co-router"])
    fun coroutineRouter() = coRouter {
        "/user".nest {
            POST("") { userHandler.save(it) }
            GET("") { authHandler.getAuthenticatedUser(it) }
            GET("/{id}") { userHandler.findById(it) }
        }
    }

}