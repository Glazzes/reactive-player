package com.rxplayer.rxplayer.security.util

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.core.ResolvableType
import org.springframework.core.codec.Hints
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.awt.image.DataBuffer
import java.time.LocalDateTime
import java.util.*

class CustomEntryPoint : ServerAuthenticationEntryPoint {

    override fun commence(exchange: ServerWebExchange, ex: AuthenticationException): Mono<Void> {
        exchange.response.statusCode = HttpStatus.BAD_REQUEST

        val response = ErrorResponse(LocalDateTime.now(), ex.message)
        val buffer = Jackson2JsonEncoder().encodeValue(
            response,
            exchange.response.bufferFactory(),
            ResolvableType.forClass(ErrorResponse::class.java),
            MediaType.APPLICATION_JSON,
            Collections.emptyMap())

        return exchange.response.writeWith(Mono.just(buffer))
    }

}

data class ErrorResponse(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    val timeStamp: LocalDateTime,
    val message: String?
)