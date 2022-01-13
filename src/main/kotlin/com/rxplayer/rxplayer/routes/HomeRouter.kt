package com.rxplayer.rxplayer.routes

import com.rxplayer.rxplayer.repositories.CoroutineUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.core.io.FileSystemResource
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait
import org.springframework.web.reactive.function.server.coRouter
import java.nio.file.Paths

@Component
class HomeRouter {

    @Autowired private lateinit var testRepository: CoroutineUserRepository

    @Bean(name = ["home-coroutine-router"])
    fun coroutineRouter() = coRouter {
        "/home".nest {
            GET("/test") {
                val user = testRepository.findByUsername("glaze")

                user?.let {
                    ServerResponse.ok().bodyValueAndAwait(user)
                } ?: ServerResponse.notFound().buildAndAwait()
            }
            GET("/"){ ServerResponse.ok().bodyValueAndAwait("Welcome to RX Player") }
            GET("/file"){
                ServerResponse.ok()
                    .bodyValueAndAwait(FileSystemResource(Paths.get("/home/glaze/Escritorio/cheetah.jpg")))
            }
        }
    }

}