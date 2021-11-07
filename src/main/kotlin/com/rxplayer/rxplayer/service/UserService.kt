package com.rxplayer.rxplayer.service

import com.rxplayer.rxplayer.dto.input.SignupRequest
import com.rxplayer.rxplayer.dto.output.CreatedUserDTO
import com.rxplayer.rxplayer.entities.User
import com.rxplayer.rxplayer.repositories.UserRepository
import kotlinx.coroutines.reactive.asFlow
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono
import java.net.URI

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
){

    suspend fun save(request: ServerRequest): ServerResponse {
        val savedUser = request.awaitBody<SignupRequest>()
        val createdUser = Mono.just(savedUser)
            .map {
                User(username = it.username,
                    nickName = it.username,
                    email = it.email,
                    password = passwordEncoder.encode(it.password),
                    profilePicture = "")
            }
            .flatMap { userRepository.save(it) }
            .map { CreatedUserDTO(it.id, it.username, it.profilePicture) }

        return ServerResponse.status(HttpStatus.CREATED)
            .bodyAndAwait(createdUser.asFlow())
    }

}