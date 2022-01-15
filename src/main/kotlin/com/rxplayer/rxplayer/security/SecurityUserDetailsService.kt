package com.rxplayer.rxplayer.security

import com.rxplayer.rxplayer.exception.NotFoundException
import com.rxplayer.rxplayer.repositories.UserRepository
import kotlinx.coroutines.reactor.mono
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Component
class SecurityUserDetailsService(private val userRepository: UserRepository): ReactiveUserDetailsService {

    override fun findByUsername(email: String): Mono<UserDetails> {
        return mono { userRepository.findByEmail(email) }
            .map<UserDetails>{ SecurityUserAdapter(it) }
            .switchIfEmpty { Mono.error(NotFoundException("No user found")) }
    }

}