package com.rxplayer.rxplayer.service

import com.rxplayer.rxplayer.dto.input.SignupRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import reactor.core.publisher.Mono

@Component
class SongService {

    fun sayHello(name: String): Mono<ServerResponse>{
        return Mono.just("Hello there $name")
            .flatMap {
                ServerResponse.ok()
                    .body(BodyInserters.fromValue(it))
            }
    }

}