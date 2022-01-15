package com.rxplayer.rxplayer.security.jwt

import com.rxplayer.rxplayer.exception.InvalidJwtException
import com.rxplayer.rxplayer.security.BearerToken
import com.rxplayer.rxplayer.security.SecurityUserAdapter
import com.rxplayer.rxplayer.util.JwtUtil
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationManager(
    private val userDetailsService: ReactiveUserDetailsService,
    private val jwtUtil: JwtUtil
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        return Mono.just(authentication)
            .filter { it is BearerToken }
            .cast(BearerToken::class.java)
            .flatMap { mono { validateToken(it) } }
            .onErrorMap { InvalidJwtException(it.message) }
    }

    private suspend fun validateToken(token: BearerToken): Authentication {
        val email = jwtUtil.getSubject(token)
        val user = userDetailsService.findByUsername(email).awaitSingleOrNull()

        if(jwtUtil.validate(token.value, user as SecurityUserAdapter?)){
            return UsernamePasswordAuthenticationToken(user!!, user.password, user.authorities)
        }

        throw IllegalArgumentException("Invalid bearer token")
    }

}