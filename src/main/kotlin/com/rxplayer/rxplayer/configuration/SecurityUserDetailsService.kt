package com.rxplayer.rxplayer.configuration

import com.rxplayer.rxplayer.repositories.ReactiveUserRepository
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SecurityUserDetailsService(private val userRepository: ReactiveUserRepository): ReactiveUserDetailsService {

    // Are you confused by the unnecessary typing? Yeah me too, this makes kotlin compiler happy
    override fun findByUsername(username: String): Mono<UserDetails> {
        return userRepository.findByUsernameAndAuthenticationProvider(username, AuthenticationProvider.RX_PLAYER)
            .map<UserDetails> { SecurityUserAdapter(it)}
            .switchIfEmpty(Mono.error(UsernameNotFoundException("User with username $username does not exists")))
    }

}