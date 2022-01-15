package com.rxplayer.rxplayer.security.jwt

import com.rxplayer.rxplayer.security.BearerToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtServerConverter : ServerAuthenticationConverter {

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        return Mono.justOrEmpty(exchange.request.cookies["Access-Token"])
            .filter { it != null }
            .map { it[0].value }
            .flatMap{ Mono.just(BearerToken(it)) }
    }

}