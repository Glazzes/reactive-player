package com.rxplayer.rxplayer.repositories

import com.rxplayer.rxplayer.entities.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface UserRepository : ReactiveMongoRepository<User, String> {
    fun findByUsername(username: String): Mono<User>

    // Do not trust intellij warnings!!!
    fun existsByUsername(username: String): Mono<Boolean>
    fun existsByEmail(email: String): Mono<Boolean>
}