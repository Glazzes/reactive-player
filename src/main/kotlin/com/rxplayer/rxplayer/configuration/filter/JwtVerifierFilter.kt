package com.rxplayer.rxplayer.configuration.filter

import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

class JwtVerifierFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val requestUrl = exchange.request.uri.toString()
        val requestMethod = exchange.request.method.toString()

        return Mono.empty()
    }
}