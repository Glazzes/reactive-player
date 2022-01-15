package com.rxplayer.rxplayer.repositories

import com.rxplayer.rxplayer.entities.User
import com.rxplayer.rxplayer.security.AuthenticationProvider
import org.springframework.data.repository.kotlin.CoroutineSortingRepository

interface UserRepository : CoroutineSortingRepository<User, String> {
    suspend fun findByEmail(username: String): User?
    suspend fun existsByUsername(username: String): Boolean
    suspend fun existsByEmail(email: String): Boolean
    suspend fun findByEmailAndAuthenticationProvider(email: String, authenticationProvider: AuthenticationProvider): User?
}