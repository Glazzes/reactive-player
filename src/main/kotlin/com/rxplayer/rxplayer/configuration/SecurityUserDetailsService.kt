package com.rxplayer.rxplayer.configuration

import com.rxplayer.rxplayer.repositories.UserRepository
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class SecurityUserDetailsService(private val userRepository: UserRepository): ReactiveUserDetailsService {

    override fun findByUsername(username: String): Mono<UserDetails> {
        return userRepository.findByUsername(username)
            .handle {user, sink ->
                if(user == null){
                    sink.error(UsernameNotFoundException("No user was found with username $username"))
                }

                sink.next(SecurityUserAdapter(user))
            }
    }

}