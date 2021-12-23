package com.rxplayer.rxplayer.configuration

import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class CustomEntryPoint : ServerAuthenticationEntryPoint {

    override fun commence(exchange: ServerWebExchange, ex: AuthenticationException): Mono<Void> {
        return Mono.just(exchange)
            .doOnNext {
                it.attributes["message"] = ex.message
                it.attributes["test"] = "This is a testing message"
                it.response.statusCode = HttpStatus.BAD_REQUEST
            }
            .then()
    }
}