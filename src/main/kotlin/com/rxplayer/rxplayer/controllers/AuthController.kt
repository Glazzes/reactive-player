package com.rxplayer.rxplayer.controllers

import com.rxplayer.rxplayer.dto.input.UserCredentials
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/auth")
class AuthController {

    fun login(@RequestBody credentials: UserCredentials): Mono<ServerResponse> {
        return ServerResponse.ok().build()
    }

}