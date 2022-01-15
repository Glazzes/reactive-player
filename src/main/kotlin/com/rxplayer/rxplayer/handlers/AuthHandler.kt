package com.rxplayer.rxplayer.handlers

import com.rxplayer.rxplayer.dto.input.UserCredentials
import com.rxplayer.rxplayer.repositories.UserRepository
import com.rxplayer.rxplayer.security.AuthenticationProvider
import com.rxplayer.rxplayer.util.JwtUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.buildAndAwait
import java.time.Duration

@Component
class AuthHandler(
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
){

    suspend fun login(serverRequest: ServerRequest): ServerResponse {
        val credentials = serverRequest.awaitBody<UserCredentials>()
        val user = userRepository.findByEmailAndAuthenticationProvider(credentials.email, AuthenticationProvider.RX_PLAYER) ?:
            throw UsernameNotFoundException("There's not account associated with this username")

        if(!passwordEncoder.matches(credentials.password, user.password)){
            throw BadCredentialsException("User credentials are invalid")
        }

        val jwt = jwtUtil.create(user.email)
        val jwtCookie = ResponseCookie.fromClientResponse("Access-Token", jwt)
            .httpOnly(true)
            .path("/")
            .maxAge(Duration.ofHours(1L))
            .build()

        return ServerResponse.status(HttpStatus.NO_CONTENT)
            .header("Set-Cookie", jwtCookie.toString())
            .buildAndAwait()
    }

    suspend fun logout(serverRequest: ServerRequest): ServerResponse {
        val deleteAuthCookie = ResponseCookie.fromClientResponse("Access-Token", "")
            .maxAge(Duration.ofSeconds(0L))
            .httpOnly(true)
            .path("/")
            .build()

        return ServerResponse.status(HttpStatus.NO_CONTENT)
            .header("Set-Cookie", deleteAuthCookie.toString())
            .buildAndAwait()
    }

}