package com.rxplayer.rxplayer.requesthandlers

import com.rxplayer.rxplayer.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class UserRequestHandler(private val userService: UserService){

    @Bean
    fun userRouter() = coRouter {
        "/user".nest {
            POST("") { userService.save(it) }
            GET("/{name}") { userService.sayHello(it) }
        }
    }

    /*
    @Bean
    fun save(): RouterFunction<ServerResponse>{
        return route(POST("/user")) {
            val signUpRequest = it.bodyToMono(SignupRequest::class.java)
            ServerResponse.noContent()
                .build()
        }
    }
     */


}