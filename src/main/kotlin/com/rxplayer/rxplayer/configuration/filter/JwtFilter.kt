package com.rxplayer.rxplayer.configuration.filter

import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class JwtFilter : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        return Mono.just("")
            .doOnNext { chain.filter(exchange) }
            .then()
    }

}