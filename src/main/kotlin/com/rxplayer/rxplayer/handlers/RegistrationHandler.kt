package com.rxplayer.rxplayer.handlers

import com.rxplayer.rxplayer.repositories.UserRepository
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.queryParamOrNull
import java.lang.IllegalArgumentException

@Component
class RegistrationHandler(private val userRepository: UserRepository){

    suspend fun existsByUsername(serverRequest: ServerRequest): ServerResponse {
        val username = serverRequest.queryParamOrNull("username") ?:
            throw IllegalArgumentException("Username parameter is required")
        val exists = userRepository.existsByUsername(username)
        return ServerResponse.ok()
            .bodyValueAndAwait(exists)
    }

    suspend fun existsByEmail(serverRequest: ServerRequest): ServerResponse {
        val email = serverRequest.queryParamOrNull("email") ?:
        throw IllegalArgumentException("Email parameter is required")
        val exists = userRepository.existsByUsername(email)
        return ServerResponse.ok()
            .bodyValueAndAwait(exists)
    }

}