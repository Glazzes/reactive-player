package com.rxplayer.rxplayer.service

import com.rxplayer.rxplayer.configuration.SecurityUserAdapter
import com.rxplayer.rxplayer.dto.input.SignupRequest
import com.rxplayer.rxplayer.dto.output.CreatedUserDTO
import com.rxplayer.rxplayer.dto.output.FindUserDTO
import com.rxplayer.rxplayer.entities.User
import com.rxplayer.rxplayer.exception.NotFoundException
import com.rxplayer.rxplayer.repositories.UserRepository
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
){

    suspend fun save(request: ServerRequest): ServerResponse {
        val savedUser = request.awaitBody<SignupRequest>()
        println(request.headers())
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
            .bodyValueAndAwait(createdUser.awaitSingle())
    }

    suspend fun getAuthenticatedUser(serverRequest: ServerRequest): ServerResponse {
        val authenticatedUser = ReactiveSecurityContextHolder.getContext()
            .map { (it.authentication.principal as SecurityUserAdapter).user }
            .map { FindUserDTO(it.id, it.nickName, it.email, it.profilePicture) }

        return ServerResponse.ok()
            .bodyValueAndAwait(authenticatedUser.awaitSingle())
    }

    suspend fun findById(serverRequest: ServerRequest): ServerResponse {
        val id = serverRequest.pathVariable("id")
        val user = userRepository.findById(id)
            .map { FindUserDTO(it.id, it.username, it.nickName, it.profilePicture) }
            .switchIfEmpty(
                Mono.error(NotFoundException("User with id $id was not found."))
            )

        return ServerResponse.ok()
            .bodyValueAndAwait(user.awaitSingle())
    }

}