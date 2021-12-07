package com.rxplayer.rxplayer.configuration

import com.rxplayer.rxplayer.repositories.UserRepository
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Component
class SecurityUserDetailsService(private val userRepository: UserRepository): ReactiveUserDetailsService {

    /*
    This stupid unnecessary cast makes compiler compiler happy, so be it...
     */
    override fun findByUsername(username: String): Mono<UserDetails> {
        return userRepository.findByUsername(username)
            .map { SecurityUserAdapter(it) as UserDetails }
            .switchIfEmpty { Mono.error(UsernameNotFoundException("User with username $username does not exists")) }
    }

}