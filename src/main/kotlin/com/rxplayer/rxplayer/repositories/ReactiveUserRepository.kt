package com.rxplayer.rxplayer.repositories

import com.rxplayer.rxplayer.configuration.AuthenticationProvider
import com.rxplayer.rxplayer.entities.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface ReactiveUserRepository : ReactiveMongoRepository<User, String> {
    fun findByUsernameAndAuthenticationProvider(username: String, authenticationProvider: AuthenticationProvider): Mono<User>
    fun existsByEmail(email: String): Mono<Boolean>
}