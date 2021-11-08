package com.rxplayer.rxplayer.requesthandlers

import com.rxplayer.rxplayer.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.router

@Configuration
class UserRequestHandler(private val userService: UserService){

    @Bean(name = ["user-co-router"])
    fun coroutineRouter() = coRouter {
        "/user".nest {
            POST("") { userService.save(it) }
        }
    }

    @Bean(name = ["user-mono-router"])
    fun monoRouter() = router {
        "/user".nest {
            GET("/{id}") { userService.findById(it) }
        }
    }

}