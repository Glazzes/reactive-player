package com.rxplayer.rxplayer.repositories

import com.rxplayer.rxplayer.configuration.AuthenticationProvider
import com.rxplayer.rxplayer.entities.User
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import reactor.core.publisher.Mono

interface CoroutineUserRepository : CoroutineSortingRepository<User, String> {
    suspend fun findByUsername(username: String): User?
    suspend fun existsByUsername(username: String): Boolean
    suspend fun existsByEmail(email: String): Boolean
}