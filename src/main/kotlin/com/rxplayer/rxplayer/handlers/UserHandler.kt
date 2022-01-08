package com.rxplayer.rxplayer.handlers

import com.rxplayer.rxplayer.configuration.AuthenticationProvider
import com.rxplayer.rxplayer.dto.input.SignupRequest
import com.rxplayer.rxplayer.dto.output.CreatedUserDTO
import com.rxplayer.rxplayer.dto.output.FindUserDTO
import com.rxplayer.rxplayer.entities.User
import com.rxplayer.rxplayer.exception.NotFoundException
import com.rxplayer.rxplayer.repositories.UserRepository
import com.rxplayer.rxplayer.validator.SignUpRequestValidator
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono

@Service
class UserHandler(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val signUpValidator: SignUpRequestValidator
){
    suspend fun save(request: ServerRequest): ServerResponse {
        val newUser = request.awaitBody<SignupRequest>()
        val errors = BeanPropertyBindingResult(newUser, SignupRequest::class.java.name)
        signUpValidator.validate(newUser, errors)

        if(errors.hasFieldErrors()){
            val customErrors: MutableMap<String, String?> = HashMap()
            for(error in errors.fieldErrors){
                customErrors[error.field] = error.code
            }

            return ServerResponse.badRequest()
                .bodyValueAndAwait(customErrors)
        }

        val savedUser = Mono.just(newUser)
            .map {
                User(username = newUser.username,
                    nickName = newUser.username,
                    email = newUser.email,
                    password = passwordEncoder.encode(newUser.password),
                    profilePicture = "",
                    authenticationProvider = AuthenticationProvider.RX_PLAYER,
                )
            }
            .flatMap { userRepository.save(it) }
            .map { CreatedUserDTO(it.id, it.username, it.profilePicture) }

        return ServerResponse.status(HttpStatus.CREATED)
            .bodyValueAndAwait(savedUser.awaitSingle())
    }

    suspend fun findById(serverRequest: ServerRequest): ServerResponse {
        val id = serverRequest.pathVariable("id")
        val user = userRepository.findById(id)
            .map { FindUserDTO(it.id, it.username, it.nickName, it.profilePicture) }
            .switchIfEmpty(
                Mono.error(NotFoundException("User with id $id was not found."))
            )

        return ServerResponse.ok()
            .bodyValueAndAwait(user.awaitSingle())
    }

}