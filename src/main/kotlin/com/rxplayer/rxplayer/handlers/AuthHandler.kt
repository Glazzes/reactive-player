package com.rxplayer.rxplayer.handlers

import com.rxplayer.rxplayer.configuration.AuthenticationProvider
import com.rxplayer.rxplayer.dto.input.UserCredentials
import com.rxplayer.rxplayer.exception.InvalidAuthenticationProviderException
import com.rxplayer.rxplayer.repositories.CoroutineUserRepository
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
    private val passwordEncoder: PasswordEncoder,
    private val userRepository: CoroutineUserRepository
){

    suspend fun login(serverRequest: ServerRequest): ServerResponse {
        val credentials = serverRequest.awaitBody<UserCredentials>()
        val user = userRepository.findByUsername(credentials.username) ?:
            throw UsernameNotFoundException("There's not account associated with this username")

        if(user.authenticationProvider != AuthenticationProvider.RX_PLAYER){
            throw InvalidAuthenticationProviderException("User can not authenticate through this provider")
        }

        if(!passwordEncoder.matches(credentials.password, user.password)){
            throw BadCredentialsException("User credentials are invalid")
        }

        val jwtCookie = ResponseCookie.fromClientResponse("Access-Token", "Epic Jwt token")
            .httpOnly(true)
            .path("/")
            .domain("http://localhost:8080")
            .maxAge(Duration.ofHours(1L))
            .build()

        return ServerResponse.status(HttpStatus.OK)
            .header("Set-Cookie", jwtCookie.toString())
            .buildAndAwait()
    }

}