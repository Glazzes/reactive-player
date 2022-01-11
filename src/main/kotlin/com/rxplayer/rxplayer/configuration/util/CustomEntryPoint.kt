package com.rxplayer.rxplayer.configuration.util

import org.springframework.core.ResolvableType
import org.springframework.core.codec.Hints
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.time.LocalDateTime

class CustomEntryPoint : ServerAuthenticationEntryPoint {

    override fun commence(exchange: ServerWebExchange, ex: AuthenticationException): Mono<Void> {
        exchange.response.statusCode = HttpStatus.BAD_REQUEST

        val response = ErrorResponse(LocalDateTime.now(), ex.localizedMessage)
        val bytes = Jackson2JsonEncoder().encodeValue(
            response,
            exchange.response.bufferFactory(),
            ResolvableType.forClass(ErrorResponse::class.java),
            MediaType.APPLICATION_JSON,
            Hints.from(Hints.LOG_PREFIX_HINT, exchange.logPrefix))

        return exchange.response.writeWith(Mono.just(bytes))
    }

}

data class ErrorResponse(
    val timeStamp: LocalDateTime,
    val message: String
)