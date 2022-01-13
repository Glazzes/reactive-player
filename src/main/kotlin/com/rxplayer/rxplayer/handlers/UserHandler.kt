package com.rxplayer.rxplayer.handlers

import com.rxplayer.rxplayer.configuration.SecurityUserAdapter
import com.rxplayer.rxplayer.dto.input.SignupRequest
import com.rxplayer.rxplayer.dto.output.created.CreatedUserDTO
import com.rxplayer.rxplayer.dto.output.find.FindUserDTO
import com.rxplayer.rxplayer.entities.User
import com.rxplayer.rxplayer.repositories.CoroutineUserRepository
import com.rxplayer.rxplayer.validator.SignUpRequestValidator
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.web.reactive.function.server.*

@Service
class UserHandler(
    private val passwordEncoder: PasswordEncoder,
    private val signUpValidator: SignUpRequestValidator,
    private val userRepository: CoroutineUserRepository
){
    suspend fun save(request: ServerRequest): ServerResponse {
        val signup = request.awaitBody<SignupRequest>()
        val errors = BeanPropertyBindingResult(signup, SignupRequest::class.java.name)
        signUpValidator.validate(signup, errors)

        if(errors.hasFieldErrors()){
            val customErrors: MutableMap<String, String?> = HashMap()
            for(error in errors.fieldErrors){
                customErrors[error.field] = error.code
            }

            return ServerResponse.badRequest()
                .bodyValueAndAwait(customErrors)
        }

        val user = User(
            username = signup.username,
            nickName = signup.username,
            email = signup.email,
            password = passwordEncoder.encode(signup.password))

        val savedUser = userRepository.save(user)
        val dto = CreatedUserDTO(savedUser.id, savedUser.username, savedUser.profilePicture)
        return ServerResponse.status(HttpStatus.CREATED)
            .bodyValueAndAwait(dto)
    }

    suspend fun findCurrentUser(serverRequest: ServerRequest): ServerResponse {
        val principal = serverRequest.awaitPrincipal() as? UsernamePasswordAuthenticationToken?
            ?: throw IllegalStateException("User is not authenticated")

        val currentUser = (principal.principal as SecurityUserAdapter).user
        return ServerResponse.ok().bodyValueAndAwait(currentUser)
    }

    suspend fun findById(serverRequest: ServerRequest): ServerResponse {
        val id = serverRequest.pathVariable("id")
        val user = userRepository.findById(id)

        return user?.let {
            val dto = FindUserDTO(it.id, it.username, it.nickName, it.profilePicture)
            ServerResponse.ok().bodyValueAndAwait(dto)
        } ?: ServerResponse.notFound().buildAndAwait()
    }

}