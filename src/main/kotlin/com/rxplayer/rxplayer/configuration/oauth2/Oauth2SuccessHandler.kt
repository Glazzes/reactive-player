package com.rxplayer.rxplayer.configuration.oauth2

import com.rxplayer.rxplayer.configuration.AuthenticationProvider
import com.rxplayer.rxplayer.entities.User
import com.rxplayer.rxplayer.exception.ResourceAlreadyExistsException
import com.rxplayer.rxplayer.repositories.ReactiveUserRepository
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.net.URI

@Component
class Oauth2SuccessHandler(private val userRepository: ReactiveUserRepository) : ServerAuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        webFilterExchange.exchange.response.headers.location = URI("http://localhost:8080/home")

        return Mono.just(authentication)
            .flatMap {
                val attributes = (authentication.principal as OAuth2User).attributes
                val email = attributes["email"] as String

                val checkIfExists = userRepository.existsByEmail(email)
                    .handle<Boolean> { exists, sink ->
                        if(exists) sink.error(ResourceAlreadyExistsException("Email $email is already taken."))
                        sink.next(exists)
                    }
                    .flatMap { saveOauth2User(attributes) }

                checkIfExists.then()
            }
    }

    fun saveOauth2User(attributes: Map<String, Any>): Mono<User> {
        val user = User(
            username = attributes["given_name"] as String,
            nickName = attributes["given_name"] as String,
            email = attributes["email"] as String,
            password = null,
            profilePicture = attributes["picture"] as String,
            authenticationProvider = AuthenticationProvider.GOOGLE)

        return userRepository.save(user)
    }

}