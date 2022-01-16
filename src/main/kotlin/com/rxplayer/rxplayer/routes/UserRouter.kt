package com.rxplayer.rxplayer.routes

import com.rxplayer.rxplayer.handlers.UserHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class UserRouter(
    private val userHandler: UserHandler
){

    @Bean(name = ["user-co-router"])
    fun coroutineRouter() = coRouter {
        "/user".nest {
            GET("/me") { userHandler.findMyself(it) }
            POST("") { userHandler.save(it) }
            GET("/{id}") { userHandler.findById(it) }
            PATCH("/{id}/edit") { userHandler.edit(it) }
        }
    }

}