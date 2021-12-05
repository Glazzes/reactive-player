package com.rxplayer.rxplayer.routes

import com.rxplayer.rxplayer.handlers.RegistrationHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter
import org.springframework.web.reactive.function.server.queryParamOrNull

@Configuration
class RegistrationHelperRouter(private val registrationHandler: RegistrationHandler) {

    @Bean(name = ["registration-handler"])
    fun coroutineRouter() = coRouter {
        "/registration".nest {
            GET("") {
                val username = it.queryParamOrNull("username")
                if(username != null){
                    registrationHandler.existsByUsername(it)
                }

                registrationHandler.existsByEmail(it)
            }
        }
    }

}