package com.rxplayer.rxplayer.security.oauth2

import com.rxplayer.rxplayer.security.AuthenticationProvider
import com.rxplayer.rxplayer.entities.User
import com.rxplayer.rxplayer.exception.ResourceAlreadyExistsException
import com.rxplayer.rxplayer.repositories.UserRepository
import com.rxplayer.rxplayer.util.JwtUtil
import kotlinx.coroutines.reactor.mono
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class Oauth2SuccessHandler(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil
) : ServerAuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        val attributes = (authentication.principal as OAuth2User).attributes
        val email = attributes["email"] as String

        return mono { userRepository.existsByEmail(email) }
            .handle<Boolean> { exists, sink ->
                if(exists) sink.error(ResourceAlreadyExistsException("Email $email is already taken."))
                sink.next(exists)
            }
            .flatMap { mono {saveOauth2User(attributes)} }
            .doOnNext {
                val token = jwtUtil.create(it.username)
                val jwtCookie = ResponseCookie.fromClientResponse("Access-Token", token)
                    .httpOnly(true)
                    .maxAge(Duration.ofHours(1L))
                    .path("/")
                    .build()

                webFilterExchange.exchange.response.headers["Set-Cookie"] = jwtCookie.toString()
            }
            .then()
    }

    suspend fun saveOauth2User(attributes: Map<String, Any>): User {
        val user = User(
            username = attributes["given_name"] as String,
            email = attributes["email"] as String,
            password = null,
            profilePicture = attributes["picture"] as String,
            authenticationProvider = AuthenticationProvider.GOOGLE)

        return userRepository.save(user)
    }

}