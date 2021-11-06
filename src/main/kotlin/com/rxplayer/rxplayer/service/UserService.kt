package com.rxplayer.rxplayer.service

import com.rxplayer.rxplayer.dto.input.SignupRequest
import com.rxplayer.rxplayer.entities.User
import com.rxplayer.rxplayer.repositories.UserRepository
import kotlinx.coroutines.reactive.asFlow
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.*
import java.net.URI

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
){

    suspend fun sayHello(request: ServerRequest): ServerResponse {
        val name = request.pathVariable("name")
        return ServerResponse.created(URI(""))
            .bodyValueAndAwait("Hello world $name")
    }

    suspend fun save(request: ServerRequest): ServerResponse {
        val signupRequest = request.awaitBody<SignupRequest>()
        val newUser = User(
            username = signupRequest.username,
            nickName = signupRequest.username,
            email = signupRequest.email,
            password = passwordEncoder.encode(signupRequest.password),
            profilePicture = ""
        )

        val createdUser = userRepository.save(newUser)
        return ServerResponse.status(201)
            .bodyAndAwait(createdUser.asFlow())
    }

}